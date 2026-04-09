import os

fxml_dir = r"e:\JavaFx Project\Java_Fx\resources\fxml"


replacements = [
    (b'\xc3\xa2\xe2\x80\xa0\xc2\x90', b'\xe2\x86\x90'), # ← (Arrow)
    (b'\xc3\xa2\xe2\x82\xac\xc2\xa2', b'\xe2\x80\xa2')  # • (Bullet)
]

def repair_file(filepath):
    try:
        with open(filepath, 'rb') as f:
            content = f.read()
    except Exception as e:
        print(f"Error reading {filepath}: {e}")
        return False

    original_content = content

  
    if content.startswith(b'\xef\xbb\xbf'):
        content = content[3:]

   
    for mangled, fixed in replacements:
        content = content.replace(mangled, fixed)
    
  
    try:
        text = content.decode('utf-8')
        for s_mangled, s_fixed in [("â† ", "←"), ("â€¢", "•")]:
            text = text.replace(s_mangled, s_fixed)
        final_content = text.encode('utf-8')
    except UnicodeDecodeError:
       
        final_content = content

    
    if final_content != original_content:
        with open(filepath, 'wb') as f:
            f.write(final_content)
        return True
    
    return False

print(f"Repairing FXML files in {fxml_dir}...")
count = 0
repaired_count = 0
for filename in os.listdir(fxml_dir):
    if filename.endswith(".fxml"):
        path = os.path.join(fxml_dir, filename)
        if repair_file(path):
            repaired_count += 1
            print(f"  Fixed: {filename}")
        count += 1

print(f"Finished! Processed {count} files. Corrected {repaired_count} files.")
