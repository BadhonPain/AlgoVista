import os

fxml_dir = r"e:\JavaFx Project\Java_Fx\resources\fxml"

def global_latin1_repair(filepath):
    try:
        with open(filepath, 'r', encoding='utf-8') as f:
            lines = f.readlines()
        
        repaired = False
        fixed_lines = []
        
        for line in lines:
            try:
                
                fixed_line = line.encode('latin-1').decode('utf-8')
                
                if fixed_line != line:
                    fixed_lines.append(fixed_line)
                    repaired = True
                else:
                    fixed_lines.append(line)
            except Exception:
                fixed_lines.append(line)
        
        if repaired:
            with open(filepath, 'w', encoding='utf-8') as f:
                f.writelines(fixed_lines)
            return True
            
    except Exception as e:
        print(f"Error processing {os.path.basename(filepath)}: {e}")
    return False

print("Running Global Latin-1 Restoration...")
count = 0
repaired_count = 0

for filename in os.listdir(fxml_dir):
    if filename.endswith(".fxml"):
        path = os.path.join(fxml_dir, filename)
        if global_latin1_repair(path):
            repaired_count += 1
            print(f"  Successfully restored: {filename}")
        count += 1

print(f"\nULTIMATE SUCCESS! Processed {count} files. Restored symbols in {repaired_count} additional files.")
