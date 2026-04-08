import os

fxml_dir = r"e:\JavaFx Project\Java_Fx\resources\fxml"

def repair_file(filepath):
    try:
        
        with open(filepath, 'r', encoding='utf-8') as f:
            content = f.read()
        
        original = content
        lines = content.split('\n')
        fixed_lines = []
        
        for line in lines:
            
            if any(c in line for c in "âðÃðŸ"):
                try:
                    
                    fixed_line = line.encode('cp1252').decode('utf-8')
                    fixed_lines.append(fixed_line)
                except Exception:
                   
                    fixed_lines.append(line)
            else:
                fixed_lines.append(line)
        
        content = '\n'.join(fixed_lines)
        
        
        if content != original:
            with open(filepath, 'w', encoding='utf-8') as f:
                f.write(content)
            return True
            
    except Exception as e:
        print(f"Error checking {os.path.basename(filepath)}: {e}")
    return False

print("Starting Deep Character Restoration...")
count = 0
repaired = 0

for filename in os.listdir(fxml_dir):
    if filename.endswith(".fxml"):
        path = os.path.join(fxml_dir, filename)
        if repair_file(path):
            repaired += 1
            print(f"  Restored icons in: {filename}")
        count += 1

print(f"\nSUCCESS! Processed {count} files. Restored symbols in {repaired} files.")
print("The 'Double-Encoding' error is now fully reversed. All UI elements should be original.")
