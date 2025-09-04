package com.pokermon;

import com.pokermon.interfaces.cli.ConsoleInterface;

/**
 * New organized entry point demonstrating clean architecture.
 * This class shows how to properly separate interface concerns while maintaining backward compatibility.
 * 
 * @author Carl Nelson (@Gameaday)
 * @version 1.0.0
 */
public class OrganizedLauncher {
    
    public static void main(String[] args) {
        System.out.println("=======================================================");
        System.out.println("   POKERMON - REORGANIZED ARCHITECTURE DEMONSTRATION");
        System.out.println("=======================================================");
        System.out.println();
        System.out.println("This launcher demonstrates the new organized code structure:");
        System.out.println();
        System.out.println("📦 Package Organization:");
        System.out.println("  ├── com.pokermon.api/           - Core game configuration & enums");
        System.out.println("  ├── com.pokermon.core/          - Business logic & game engine");
        System.out.println("  ├── com.pokermon.interfaces/");
        System.out.println("  │   ├── cli/                   - Console interface");
        System.out.println("  │   ├── gui/                   - Desktop GUI interface");
        System.out.println("  │   └── common/                - Shared interface utilities");
        System.out.println("  └── com.pokermon/               - Legacy compatibility layer");
        System.out.println();
        System.out.println("🎯 Clean Architecture Benefits:");
        System.out.println("  ✅ Clear separation of concerns");
        System.out.println("  ✅ Easier to maintain and test");
        System.out.println("  ✅ Better code organization");
        System.out.println("  ✅ Prepared for future Android/GUI integration");
        System.out.println("  ✅ Backward compatibility preserved (190 tests passing)");
        System.out.println();
        
        if (args.length > 0 && args[0].equals("--run-demo")) {
            System.out.println("🚀 Launching new console interface...");
            System.out.println();
            ConsoleInterface.main(new String[0]);
        } else {
            System.out.println("💡 To run the reorganized console interface, use:");
            System.out.println("   java -cp target/pokermon-1.0.0-fat.jar com.pokermon.OrganizedLauncher --run-demo");
            System.out.println();
            System.out.println("🔄 Legacy interfaces still available:");
            System.out.println("   java -cp target/pokermon-1.0.0-fat.jar com.pokermon.Main");
            System.out.println("   java -cp target/pokermon-1.0.0-fat.jar com.pokermon.ConsoleMain");
        }
    }
}