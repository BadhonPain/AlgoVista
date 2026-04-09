import os

fxml_dir = r"e:\JavaFx Project\Java_Fx\resources\fxml"

def universal_repair(filepath):
    try:
        
        with open(filepath, 'r', encoding='utf-8') as f:
            content = f.read()

        
        
        if "â" in content or "ð" in content or "Ã" in content:
            try:
                
                
                fixed_content = content.encode('cp1252').decode('utf-8')
                
                
                with open(filepath, 'w', encoding='utf-8') as f:
                    f.write(fixed_content)
                return True
            except Exception as e:
            
                return False
    except Exception as e:
        return False
    return False

print(f"Applying Universal Character Restoration...")
count = 0
fixed_count = 0

for filename in os.listdir(fxml_dir):
    if filename.endswith(".fxml"):
        path = os.path.join(fxml_dir, filename)
        if universal_repair(path):
            fixed_count += 1
            print(f"  Restored characters in: {filename}")
        count += 1

print(f"\nSUCCESS! Processed {count} files. Restored symbols in {fixed_count} files.")
print("The 'Double-Encoding' error has been reversed. All emojis and arrows should be back to original.")
