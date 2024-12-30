import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.util.*

data class Config(
    val token: String,
    val username: String,
)

fun main() {
    val config = loadConfig("config.json")
    val repositories = fetchRepositories(config)

    println("Available repositories:")
    repositories.forEachIndexed { index, repo -> println("$index: ${repo["name"]}") }

    print("Select a repository by index: ")
    val selectedIndex = readLine()?.toIntOrNull()
    val selectedRepo =
        repositories.getOrNull(selectedIndex ?: -1)
            ?: error("Invalid selection.")

    val repoName = selectedRepo["name"] as String
    println("Selected repository: $repoName")

    val branchName = "add-hello-file"
    createBranch(config, repoName, branchName)
    addFileAndCommit(config, repoName, branchName, "Hello.txt", "Hello world")
    createPullRequest(config, repoName, branchName, "Add Hello.txt", "This PR adds Hello.txt with 'Hello world'.")
}

fun loadConfig(filePath: String): Config {
    val file = File(filePath)
    if (!file.exists()) error("Configuration file not found at $filePath")
    val mapper = jacksonObjectMapper()
    return mapper.readValue(file)
}

fun fetchRepositories(config: Config): List<Map<String, Any>> {
    val client = OkHttpClient()
    val request =
        Request
            .Builder()
            .url("https://api.github.com/user/repos")
            .header("Authorization", "token ${config.token}")
            .build()

    val response = client.newCall(request).execute()
    if (!response.isSuccessful) error("Failed to fetch repositories: ${response.body?.string()}")

    val mapper = jacksonObjectMapper()
    return mapper.readValue(response.body!!.string())
}

fun createBranch(
    config: Config,
    repoName: String,
    branchName: String,
) {
    val client = OkHttpClient()

    // Get main branch SHA
    val mainBranchRequest =
        Request
            .Builder()
            .url("https://api.github.com/repos/${config.username}/$repoName/git/refs/heads/main")
            .header("Authorization", "token ${config.token}")
            .build()

    val mainBranchResponse = client.newCall(mainBranchRequest).execute()
    if (!mainBranchResponse.isSuccessful) error("Failed to fetch branch info: ${mainBranchResponse.body?.string()}")
    val mainSha = jacksonObjectMapper().readTree(mainBranchResponse.body!!.string())["object"]["sha"].asText()

    // Create new branch
    val createBranchJson =
        """
        {
            "ref": "refs/heads/$branchName",
            "sha": "$mainSha"
        }
        """.trimIndent()

    val createBranchRequest =
        Request
            .Builder()
            .url("https://api.github.com/repos/${config.username}/$repoName/git/refs")
            .header("Authorization", "token ${config.token}")
            .post(createBranchJson.toRequestBody("application/json".toMediaTypeOrNull()))
            .build()

    val createBranchResponse = client.newCall(createBranchRequest).execute()
    if (!createBranchResponse.isSuccessful) error("Failed to create branch: ${createBranchResponse.body?.string()}")
}

fun addFileAndCommit(
    config: Config,
    repoName: String,
    branchName: String,
    fileName: String,
    content: String,
) {
    val client = OkHttpClient()

    val encodedContent = Base64.getEncoder().encodeToString(content.toByteArray())
    val addFileJson =
        """
        {
            "message": "Add $fileName",
            "content": "$encodedContent",
            "branch": "$branchName"
        }
        """.trimIndent()

    val addFileRequest =
        Request
            .Builder()
            .url("https://api.github.com/repos/${config.username}/$repoName/contents/$fileName")
            .header("Authorization", "token ${config.token}")
            .put(addFileJson.toRequestBody("application/json".toMediaTypeOrNull()))
            .build()

    val addFileResponse = client.newCall(addFileRequest).execute()
    if (!addFileResponse.isSuccessful) error("Failed to add file: ${addFileResponse.body?.string()}")
}

fun createPullRequest(
    config: Config,
    repoName: String,
    branchName: String,
    title: String,
    body: String,
) {
    val client = OkHttpClient()

    val pullRequestJson =
        """
        {
            "title": "$title",
            "body": "$body",
            "head": "$branchName",
            "base": "main"
        }
        """.trimIndent()

    val pullRequest =
        Request
            .Builder()
            .url("https://api.github.com/repos/${config.username}/$repoName/pulls")
            .header("Authorization", "token ${config.token}")
            .post(pullRequestJson.toRequestBody("application/json".toMediaTypeOrNull()))
            .build()

    val pullRequestResponse = client.newCall(pullRequest).execute()
    if (!pullRequestResponse.isSuccessful) error("Failed to create pull request: ${pullRequestResponse.body?.string()}")

    val prUrl = jacksonObjectMapper().readTree(pullRequestResponse.body!!.string())["html_url"].asText()
    println("Pull request created successfully: $prUrl")
}
