#!/bin/bash

# Directory to save the files
output_dir="/root/jb_test_assessment/app/src/main/resources"
mkdir -p "$output_dir"

# Function to generate Lorem Ipsum text
generate_lorem_ipsum() {
  curl -s "https://loripsum.net/api/100/short" | sed 's/<[^>]*>//g'
}

# Generate 10 files with 100 random words each
for i in $(seq 1 10); do
  # Generate Lorem Ipsum text without <p> tags
  lorem_ipsum=$(generate_lorem_ipsum)

  # Process the text to get exactly 100 words
  echo "$lorem_ipsum" | tr ' ' '\n' | shuf | head -n 100 | tr '\n' ' ' > "$output_dir/file_$i.txt"

  # Ensure the file ends with a newline
  echo "" >> "$output_dir/file_$i.txt"
done

echo "10 files with 100 random words each (without <p> tags) have been generated in '$output_dir'."
