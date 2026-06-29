# Stop tracking build artifacts / dependencies / dumps that should be gitignored.
# Run this from inside the "Fast Trade" repo root (where this file lives).
# It only UNSTAGES/UNTRACKS files from git's index - nothing is deleted from disk.
# After running, open GitHub Desktop, review the change, and commit.

$ErrorActionPreference = "Continue"

Write-Host "Removing tracked build artifacts / dependencies from git index..." -ForegroundColor Cyan

$paths = @(
    "frontend/node_modules",
    "frontend/dist",
    "frontend/.angular",
    "android/build",
    "android/app/build",
    "android/.gradle",
    "android/.idea",
    "android/local.properties",
    "backend/target"
)

foreach ($p in $paths) {
    if (Test-Path $p) {
        git rm -r --cached "$p" --quiet 2>$null
        Write-Host "  untracked: $p"
    } else {
        Write-Host "  skip (not found): $p"
    }
}

# .hprof crash dumps (any location)
Get-ChildItem -Path . -Filter "*.hprof" -Recurse -ErrorAction SilentlyContinue | ForEach-Object {
    git rm --cached $_.FullName --quiet 2>$null
    Write-Host "  untracked: $($_.FullName)"
}

Write-Host "`nStaging .gitignore files..." -ForegroundColor Cyan
git add .gitignore
git add frontend/.gitignore
git add android/.gitignore

Write-Host "`nDone. Now check GitHub Desktop:" -ForegroundColor Green
Write-Host "  - Changes should drop from ~38k to a small, real list."
Write-Host "  - Review and click Commit when ready."
