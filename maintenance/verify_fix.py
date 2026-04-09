import os

filepath = r"e:\JavaFx Project\Java_Fx\resources\fxml\BSTOperations.fxml"

def test_fix():
    try:
       
        with open(filepath, 'r', encoding='utf-8') as f:
            content = f.read()
        
        
        mangled_line = ""
        for line in content.split('\n'):
            if "ðŸŽ" in line or "â—€" in line:
                mangled_line = line.strip()
                break
        
        if not mangled_line:
            
            for line in content.split('\n'):
                if "â" in line or "ð" in line:
                    mangled_line = line.strip()
                    break
        
        if not mangled_line:
            print("No mangled line found in BSTOperations.fxml.")
            return

        print(f"Mangled: {mangled_line}")
        
        
        try:
            fixed_line = mangled_line.encode('cp1252').decode('utf-8')
            print(f"Fixed:   {fixed_line}")
        except Exception as e:
            print(f"Error during repair logic: {e}")
            
    except Exception as e:
        print(f"Error reading file: {e}")

test_fix()
