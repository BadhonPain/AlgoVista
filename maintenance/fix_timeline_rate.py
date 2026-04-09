import os, re

target_dir = r"E:\JavaFx Project\Java_Fx\src\com\AlgoVista"

def patch():
    count = 0
    for root, dirs, files in os.walk(target_dir):
        for f in files:
            if not f.endswith(".java") or f == "SettingsManager.java":
                continue
            path = os.path.join(root, f)
            with open(path, "r", encoding="utf-8") as file:
                content = file.read()

            changed = False

            # Fix: timeline.setRate(speedSlider.getValue()) -> exponential
            new_content = re.sub(
                r'((?:autoTimeline|timeline)\.setRate\()(speedSlider\.getValue\(\))(\))',
                r'\1com.AlgoVista.utils.SettingsManager.getTimelineRate(speedSlider.getValue())\3',
                content
            )

            # Fix TreeTraversalController: 800.0 / speedSlider.getValue() 
            new_content = re.sub(
                r'800\.0\s*/\s*speedSlider\.getValue\(\)',
                r'800.0 * com.AlgoVista.utils.SettingsManager.getSleepMultiplier()',
                new_content
            )

            # Fix recursion controllers: delay = (long)(X / speedSlider.getValue())
            new_content = re.sub(
                r'delay\s*=\s*\(long\)\s*\(\s*(\d+)\s*/\s*speedSlider\.getValue\(\)\s*\)',
                r'delay = (long)((\1) * com.AlgoVista.utils.SettingsManager.getSleepMultiplier())',
                new_content
            )

            # Fix any remaining: timeline.setRate(animationSpeed) style if animationSpeed = speedSlider
            new_content = re.sub(
                r'((?:autoTimeline|timeline)\.setRate\()(animationSpeed)(\))',
                r'\1com.AlgoVista.utils.SettingsManager.getTimelineRate(animationSpeed)\3',
                new_content
            )

            if new_content != content:
                with open(path, "w", encoding="utf-8") as file:
                    file.write(new_content)
                count += 1
                print(f"Patched timeline rate in {f}")

    print(f"\nTotal files patched: {count}")

if __name__ == "__main__":
    patch()
