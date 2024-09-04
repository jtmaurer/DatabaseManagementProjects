# Define the source and binary directories
$srcDir = "src"
$binDir = "bin"

# Define the target file (this can be changed as needed)
$targetFile = "MovieDB.java"

# Define all dependent files
$dependentFiles = @("KeyType.java", "Table.java")  # Add dependencies here

# Ensure the bin directory exists
if (-not (Test-Path $binDir)) {
    New-Item -ItemType Directory -Path $binDir
}

# Create a list of all files to compile
$filesToCompile = $dependentFiles + $targetFile
$fullFilePaths = $filesToCompile | ForEach-Object { Join-Path $srcDir $_ }

# Compile all Java files, placing the output in the bin directory
javac --enable-preview -source 22 -d $binDir $fullFilePaths