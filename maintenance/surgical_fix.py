import os

fxml_dir = r"e:\JavaFx Project\Java_Fx\resources\fxml"


replacements = [
    
    (b'\xc3\xa2\xc5\xa1\xe2\x84\xa2\xc3\xaf\xc2\xb8\xc2\x8f', b'\xe2\x9a\x99\xef\xb8\x8f'),
    
    (b'\xc3\xa2\xc5\x93\xc2\x8f\xc3\xaf\xc2\xb8\xc2\x8f', b'\xf0\x9f\x93\x9d\xef\xb8\x8f'),
    
    (b'\xc3\xb0\xc5\xb8\xe2\x80\x9d\xc2\x91', b'\xf0\x9f\x97\x91\xef\xb8\x8f'),
    
    (b'\xc3\x82\xc2\xb7', b'\xc2\xb7'),
    
    (b'\xc3\x82\xc2\xa0\xc3\x82\xc2\xb7', b'\x20\xc2\xb7')
]

def surgical_repair(filepath):
    try:
        with open(filepath, 'rb') as f:
            content = f.read()
        
        original = content
        
        for mangled, fixed in replacements:
            content = content.replace(mangled, fixed)
            
        if content != original:
            with open(filepath, 'wb') as f:
                f.write(content)
            return True
    except Exception as e:
        print(f"Error repairing {os.path.basename(filepath)}: {e}")
    return False

print("Applying Surgical Icon Restoration...")
repaired = 0

for filename in os.listdir(fxml_dir):
    if filename.endswith(".fxml"):
        if surgical_repair(os.path.join(fxml_dir, filename)):
            repaired += 1
            print(f"  Surgically restored icons in: {filename}")

print(f"\nFINISHING TOUCHES COMPLETE! Surgically restored icons in {repaired} files.")
