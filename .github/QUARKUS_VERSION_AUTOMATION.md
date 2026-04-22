# Quarkus Version Automation

This repository uses GitHub Actions and Dependabot to automatically manage Quarkus version updates across multiple projects.

## Overview

The automation handles:
- **Automatic Quarkus version updates** via Dependabot for both `quarkus-eda-demo` and `quarkus-eda-knative-demo`
- **Version synchronization** across configuration files
- **Container image building and pushing** to Quay.io
- **Auto-merging** of successful Dependabot PRs

## Components

### 1. Dependabot Configuration (`.github/dependabot.yml`)

Dependabot monitors both projects separately and creates PRs when new Quarkus versions are available:

- **Schedule**: Weekly on Mondays at 9:00 AM
- **Scope**: Groups all Quarkus-related dependencies together
- **Labels**: Automatically tags PRs with project-specific labels

### 2. GitHub Actions Workflow (`.github/workflows/quarkus-version-sync.yml`)

The workflow consists of three jobs that run sequentially:

#### Job 1: `sync-versions`
- Detects which project Dependabot updated
- Extracts the new Quarkus version from `pom.xml`
- Updates version references in:
  - `quarkus-eda-demo/src/main/resources/application.properties` (line: `quarkus.container-image.tag`)
  - `quarkus-eda-demo/kube/02-keda-deployment.yaml` (line: `image: quay.io/danieloh30/quarkus-eda-demo:VERSION`)
- Commits changes back to the PR branch

#### Job 2: `build-and-push`
- Builds the `quarkus-eda-demo` project using Maven
- Runs: `./mvnw clean package -DskipTests`
- Pushes the new container image to Quay.io automatically
- Uses Quarkus Jib extension for containerization

#### Job 3: `auto-merge`
- Approves the Dependabot PR
- Enables auto-merge with squash strategy
- PR will be merged automatically once all checks pass

## Prerequisites

### Required GitHub Secrets

You must configure the following secrets in your GitHub repository:

1. **`QUAY_USERNAME`**: Your Quay.io username
2. **`QUAY_PASSWORD`**: Your Quay.io password or robot token

To add secrets:
1. Go to repository **Settings** → **Secrets and variables** → **Actions**
2. Click **New repository secret**
3. Add each secret with its value

### Required Permissions

The workflow requires the following permissions (already configured):
- `contents: write` - To commit version sync changes
- `pull-requests: write` - To approve and merge PRs

### Branch Protection (Optional but Recommended)

For auto-merge to work optimally, configure branch protection rules:

1. Go to **Settings** → **Branches** → **Add rule**
2. Branch name pattern: `main` (or your default branch)
3. Enable:
   - ✅ Require a pull request before merging
   - ✅ Require status checks to pass before merging
   - ✅ Allow auto-merge

## How It Works

### Workflow Trigger

1. **Dependabot creates a PR** updating Quarkus version in either project's `pom.xml`
2. **GitHub Actions workflow triggers** automatically on PR creation/update
3. **Version sync job runs**:
   - Detects the updated project
   - Extracts new version (e.g., `3.35.0`)
   - Updates `application.properties`: `quarkus.container-image.tag=3.35.0`
   - Updates `02-keda-deployment.yaml`: `image: quay.io/danieloh30/quarkus-eda-demo:3.35.0`
   - Commits changes to the PR branch

4. **Build job runs**:
   - Pulls latest changes from PR branch
   - Builds the project with Maven
   - Pushes new container image to Quay.io with the updated version tag

5. **Auto-merge job runs**:
   - Approves the PR
   - Enables auto-merge
   - PR merges automatically when all checks pass

### Version Synchronization

When Dependabot updates `quarkus-eda-demo/pom.xml`:

```xml
<!-- Before -->
<quarkus.platform.version>3.34.5</quarkus.platform.version>

<!-- After -->
<quarkus.platform.version>3.35.0</quarkus.platform.version>
```

The workflow automatically updates:

**File 1: `quarkus-eda-demo/src/main/resources/application.properties`**
```properties
# Before
quarkus.container-image.tag=3.34.5

# After
quarkus.container-image.tag=3.35.0
```

**File 2: `quarkus-eda-demo/kube/02-keda-deployment.yaml`**
```yaml
# Before
image: quay.io/danieloh30/quarkus-eda-demo:3.34.5

# After
image: quay.io/danieloh30/quarkus-eda-demo:3.35.0
```

## Container Image Build

The workflow uses Quarkus's built-in container image capabilities:

```bash
./mvnw clean package -DskipTests \
  -Dquarkus.container-image.build=true \
  -Dquarkus.container-image.push=true
```

Configuration from `application.properties`:
- **Registry**: `quay.io`
- **Group**: `danieloh30`
- **Name**: `quarkus-eda-demo`
- **Tag**: Automatically synced with Quarkus version
- **Push**: Enabled (requires Quay.io credentials)

## Monitoring

### Check Workflow Status

1. Go to **Actions** tab in your repository
2. Look for "Quarkus Version Sync and Build" workflow
3. Click on a run to see detailed logs for each job

### Verify Updates

After a successful run:
1. Check the merged PR for all changes
2. Verify the new image exists on Quay.io: `quay.io/danieloh30/quarkus-eda-demo:<version>`
3. Confirm version consistency across all files

## Troubleshooting

### Build Fails

**Issue**: Maven build fails during `build-and-push` job

**Solutions**:
- Check build logs in GitHub Actions
- Verify Java version compatibility (currently using JDK 25)
- Ensure all dependencies are compatible with new Quarkus version

### Image Push Fails

**Issue**: Container image fails to push to Quay.io

**Solutions**:
- Verify `QUAY_USERNAME` and `QUAY_PASSWORD` secrets are set correctly
- Check Quay.io repository permissions
- Ensure repository `danieloh30/quarkus-eda-demo` exists and is accessible

### Auto-merge Doesn't Work

**Issue**: PR is not automatically merged

**Solutions**:
- Ensure branch protection rules allow auto-merge
- Check that all required status checks pass
- Verify workflow has `pull-requests: write` permission
- Confirm `GITHUB_TOKEN` has sufficient permissions

### Version Sync Fails

**Issue**: Configuration files not updated with new version

**Solutions**:
- Check that file paths in workflow match actual file locations
- Verify sed commands work with file format
- Review commit logs in the PR

## Manual Intervention

If you need to manually trigger or modify the process:

### Manually Trigger Build

```bash
cd quarkus-eda-demo
./mvnw clean package -DskipTests \
  -Dquarkus.container-image.build=true \
  -Dquarkus.container-image.push=true
```

### Manually Update Versions

If automation fails, update these files manually:

1. `quarkus-eda-demo/pom.xml` - Line 15
2. `quarkus-eda-demo/src/main/resources/application.properties` - Line 25
3. `quarkus-eda-demo/kube/02-keda-deployment.yaml` - Line 23

## Customization

### Change Update Schedule

Edit `.github/dependabot.yml`:

```yaml
schedule:
  interval: "daily"  # Options: daily, weekly, monthly
  day: "monday"      # For weekly: monday-sunday
  time: "09:00"      # Time in UTC
```

### Modify Auto-merge Strategy

Edit `.github/workflows/quarkus-version-sync.yml`:

```yaml
# Change from squash to merge or rebase
gh pr merge --auto --merge "${{ github.event.pull_request.number }}"
# or
gh pr merge --auto --rebase "${{ github.event.pull_request.number }}"
```

### Add More Projects

To add another project to the automation:

1. Add new entry in `.github/dependabot.yml`
2. Update workflow to handle the new project's files
3. Add version sync logic for the new project's configuration files

## Security Considerations

- **Secrets**: Never commit Quay.io credentials to the repository
- **Token Permissions**: Workflow uses `GITHUB_TOKEN` with minimal required permissions
- **Auto-merge**: Only enabled for Dependabot PRs (verified by `github.actor == 'dependabot[bot]'`)
- **Build Isolation**: Each build runs in a fresh Ubuntu environment

## Support

For issues or questions:
1. Check GitHub Actions logs for detailed error messages
2. Review Dependabot PR comments for update details
3. Verify all prerequisites are met
4. Check Quarkus release notes for breaking changes

## References

- [Dependabot Documentation](https://docs.github.com/en/code-security/dependabot)
- [GitHub Actions Documentation](https://docs.github.com/en/actions)
- [Quarkus Container Images Guide](https://quarkus.io/guides/container-image)
- [Quay.io Documentation](https://docs.quay.io/)