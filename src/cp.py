import os

SOURCE_DIR = "."          # Folder to scan
OUTPUT_FILE = "merged_output.txt"

EXTENSIONS = {".py", ".java", ".js", ".ts", ".txt"}  # Add/remove as needed
IGNORED_DIRS = {"build"}  # Folders to ignore

with open(OUTPUT_FILE, "w", encoding="utf-8") as out:
    for root, dirs, files in os.walk(SOURCE_DIR):
        # Skip hidden folders and ignored folders like "build"
        dirs[:] = [
            d for d in dirs
            if not d.startswith(".") and d.lower() not in IGNORED_DIRS
        ]

        for file in files:
            if file == OUTPUT_FILE:
                continue

            file_path = os.path.join(root, file)
            ext = os.path.splitext(file)[1]

            # Only merge selected file types
            if ext in EXTENSIONS:
                out.write(f"\n{'=' * 80}\n")
                out.write(f"FILE: {file_path}\n")
                out.write(f"{'=' * 80}\n")

                try:
                    with open(file_path, "r", encoding="utf-8", errors="ignore") as f:
                        out.write(f.read())
                except Exception as e:
                    out.write(f"\n[ERROR READING FILE: {e}]\n")

                out.write("\n\n")

print(f"✅ All files merged into {OUTPUT_FILE}")
