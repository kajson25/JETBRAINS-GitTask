# GitHub Repository Automation with Kotlin

This project automates the process of connecting to a private GitHub account, listing repositories, and creating a pull request to add a file (`Hello.txt`) with the text "Hello world".

## Features
- Connect to a private GitHub account using a Personal Access Token (PAT).
- List all repositories under the account.
- Allow the user to select a repository via CLI.
- Automatically create a new branch, add a file (`Hello.txt`), and push changes.
- Create a pull request for the added file.

## Prerequisites
1. **GitHub Personal Access Token (PAT)**:
    - Generate a token from [GitHub Settings](https://github.com/settings/tokens).
    - Ensure the token has the following scopes:
        - `repo` (full control of private repositories).
2. **Kotlin Installed**:
    - Use the latest version of Kotlin and Gradle.

## Setup Instructions
1. Clone this repository:
   ```bash
   git clone https://github.com/your-username/your-repo.git
   cd your-repo
2. Create a config.json file in the project root directory with the following structure:
    ```json
    {
        "token": "your_personal_access_token",
        "username": "your_github_username"
    }
3. Build the project using Gradle:
    ```bash
    ./gradlew build
4. Run the application
    ```bash
    ./gradlew run

## How It Works
1. The application reads the GitHub credentials from a `config.json` file.
2. It uses these credentials to authenticate with the GitHub API.
3. The program fetches and displays a list of repositories associated with the account.
4. After selecting a repository, the program:
    - Creates a new branch named `add-hello-file`.
    - Adds a `Hello.txt` file containing the text "Hello world" to the new branch.
    - Pushes the branch to the repository.
    - Creates a pull request targeting the `main` branch.

## Dependencies
- **OkHttp**: A modern HTTP client for making API requests.
- **Jackson**: A powerful JSON parsing library for Kotlin.
- **Kotlin Standard Library**: Core utilities for Kotlin programming.

## Example Usage

1. After running the application, you'll see a list of repositories:
    ```yaml
    Available repositories:
    0: MyRepo
    1: AnotherRepo
    Select a repository by index:
    ```

2. Enter the index (e.g., `0`) to select a repository.

3. The application creates a pull request. You'll see the link to the PR in the console:
    ```bash
    Pull request created successfully: https://github.com/username/MyRepo/pull/1
    ```

### Notes
- Ensure your `config.json` file is not publicly accessible as it contains sensitive information.
- The created pull request will target the `main` branch by default.

### Contact
- katarina.vucicevic25@gmail.com
