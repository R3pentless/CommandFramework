# CommandFramework

[![JitPack](https://jitpack.io/v/R3pentless/commandframework.svg)](https://jitpack.io/#R3pentless/commandframework)
[![Documentation](https://img.shields.io/badge/docs-wiki-blue)](https://github.com/R3pentless/commandframework/wiki)

Annotation-based command framework for Minecraft 1.18.2 plugins.

## Features

✅ **Simple** - Annotation-based, minimal boilerplate  
✅ **Smart** - Auto tab-completion with intelligent suppliers  
✅ **Fast** - No plugin.yml entries, dynamic registration  
✅ **Safe** - Type-safe argument parsing with validation  
✅ **Flexible** - Custom parsers and suppliers support  

## Quick Example

```java
@Command(name = "give", permission = "plugin.give")
public class GiveCommand {
    
    @Default
    public void execute(Player sender,
                       @Arg("player") Player target,
                       @Arg("item") Material item,
                       @Arg("amount") @Range(min=1, max=64) int amount) {
        
        target.getInventory().addItem(new ItemStack(item, amount));
        sender.sendMessage("§aGave " + amount + "x " + item);
    }
}
```

**That's it!** No plugin.yml, automatic tab-completion, type validation - all included.

## Installation

### Gradle (Kotlin DSL)

```kotlin
repositories {
    maven("https://jitpack.io")
}

dependencies {
    implementation("com.github.R3pentless:commandframework:1.0.0")
}

tasks.shadowJar {
    relocate("pl.inh.commandframework", "yourpackage.libs.commandframework")
}
```

### Gradle (Groovy)

```groovy
repositories {
    maven { url 'https://jitpack.io' }
}

dependencies {
    implementation 'com.github.R3pentless:commandframework:1.0.0'
}

shadowJar {
    relocate 'pl.inh.commandframework', 'yourpackage.libs.commandframework'
}
```

## Quick Start

**1. Initialize the framework:**

```java
public class MyPlugin extends JavaPlugin {
    @Override
    public void onEnable() {
        CommandFramework framework = new CommandFramework(this);
        framework.register(new MyCommand());
    }
}
```

**2. Create your command:**

```java
@Command(name = "test")
public class MyCommand {
    
    @Default
    public void execute(Player player) {
        player.sendMessage("Hello!");
    }
}
```

**3. Done!** Type `/test` in-game.

## Documentation

📚 **[Full Documentation](https://github.com/R3pentless/commandframework/wiki)**

- [Installation Guide](https://github.com/R3pentless/commandframework/wiki/Home)
- [Examples](https://github.com/R3pentless/commandframework/wiki/Examples)
- [Advanced Usage](https://github.com/R3pentless/commandframework/wiki/Advanced)

## Features Overview

### Annotations

```java
@Command(name = "eco", aliases = {"money"}, permission = "plugin.eco")
@SubCommand("pay")
@Permission("plugin.eco.pay")
@Cooldown(seconds = 5)
@Arg("amount") @Range(min = 1, max = 1000)
@Optional("10")
```

### Auto Tab-Completion

Arguments get automatic tab-completion based on type:

- `Player` → Online players
- `Material` → Item names
- `World` → World names
- `GameMode` → Game modes
- Any `Enum` → Enum values
- `int`/`double` → Suggested numbers

### Custom Suppliers

```java
framework.getSupplierRegistry().register("warps", context -> 
    warpManager.getAllWarps().stream()
        .map(Warp::getName)
        .toList()
);
```

### Built-in Validation

```java
@Arg("amount") @Range(min = 1, max = 100) int amount
@Arg("player") @Optional Player target
```

## Examples

### Economy Command

```java
@Command(name = "economy", aliases = {"eco", "money"})
public class EconomyCommand {
    
    @Default
    public void balance(Player player) {
        player.sendMessage("Balance: $" + getBalance(player));
    }
    
    @SubCommand("pay")
    @Cooldown(seconds = 3)
    public void pay(Player sender,
                    @Arg("player") Player target,
                    @Arg("amount") @Range(min = 1) double amount) {
        transfer(sender, target, amount);
    }
}
```

### Admin Command

```java
@Command(name = "admin", permission = "plugin.admin")
public class AdminCommand {
    
    @SubCommand("kick")
    public void kick(CommandSender sender,
                    @Arg("player") Player target,
                    @Arg("reason") @Optional("Kicked") String reason) {
        target.kickPlayer(reason);
    }
    
    @SubCommand("gamemode")
    public void gamemode(CommandSender sender,
                        @Arg("mode") GameMode mode,
                        @Arg("player") @Optional Player target) {
        Player p = target != null ? target : (Player) sender;
        p.setGameMode(mode);
    }
}
```

More examples in the [Wiki](https://github.com/R3pentless/commandframework/wiki/Examples).

## Support

- **Issues**: [GitHub Issues](https://github.com/R3pentless/commandframework/issues)
- **Wiki**: [Documentation](https://github.com/R3pentless/commandframework/wiki)

## License

MIT License - see [LICENSE](LICENSE) file for details.

## Contributing

Contributions are welcome! Feel free to submit pull requests or open issues.

## Credits

Created by [R3pentless](https://github.com/R3pentless)
