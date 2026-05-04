# GitHub Secrets Setup Guide

## Required Secrets for Quarkus Version Sync Workflow

The `quarkus-version-sync.yml` workflow requires the following secrets to be configured in your GitHub repository to authenticate with Quay.io container registry.

### Required Secrets

1. **QUAY_USERNAME** - Your Quay.io username
2. **QUAY_PASSWORD** - Your Quay.io password or robot account token

### How to Set Up Secrets

#### Step 1: Get Your Quay.io Credentials

**Option A: Use Your Personal Account**
- Username: Your Quay.io username
- Password: Your Quay.io password

**Option B: Use a Robot Account (Recommended for CI/CD)**
1. Log in to [Quay.io](https://quay.io)
2. Navigate to your organization or user account
3. Go to "Robot Accounts" section
4. Create a new robot account with push permissions to the `danieloh30/quarkus-eda-demo` repository
5. Copy the robot account username (format: `organization+robotname`)
6. Copy the robot account token

#### Step 2: Add Secrets to GitHub Repository

1. Go to your GitHub repository: `https://github.com/YOUR_USERNAME/eda-serverless-java`
2. Click on **Settings** tab
3. In the left sidebar, click **Secrets and variables** → **Actions**
4. Click **New repository secret**
5. Add the first secret:
   - Name: `QUAY_USERNAME`
   - Value: Your Quay.io username or robot account name
   - Click **Add secret**
6. Add the second secret:
   - Name: `QUAY_PASSWORD`
   - Value: Your Quay.io password or robot account token
   - Click **Add secret**

### Verify Setup

After adding the secrets, the workflow will be able to:
- Authenticate with Quay.io registry
- Build container images
- Push images to `quay.io/danieloh30/quarkus-eda-demo`

### Troubleshooting

**Error: "Username and password required"**
- This means the secrets are not configured or are empty
- Verify both `QUAY_USERNAME` and `QUAY_PASSWORD` are set in repository secrets
- Check that the secret names match exactly (case-sensitive)

**Error: "Authentication failed"**
- Verify your Quay.io credentials are correct
- If using a robot account, ensure it has push permissions
- Check that the robot account has access to the target repository

**Error: "Permission denied"**
- Ensure the Quay.io account has write access to the repository
- For robot accounts, verify the permissions include "Write" for the repository

### Security Best Practices

1. **Use Robot Accounts**: Create dedicated robot accounts for CI/CD instead of using personal credentials
2. **Limit Permissions**: Grant only the minimum required permissions (push to specific repositories)
3. **Rotate Credentials**: Regularly rotate robot account tokens
4. **Monitor Usage**: Review robot account activity logs in Quay.io

### Workflow Behavior

The workflow is triggered when:
- Dependabot creates a PR updating `pom.xml` files
- The workflow will:
  1. Sync Quarkus versions across configuration files
  2. Build the container image
  3. Push to Quay.io (requires authentication)
  4. Auto-merge the PR if successful

The `logout: true` parameter ensures the workflow logs out from Quay.io after pushing the image, which is a security best practice.