# CommandFramework

[![](https://jitpack.io/v/R3pentless/commandframework.svg)](https://jitpack.io/#R3pentless/commandframework)

Easy command framework for Minecraft 1.18.2 plugins.

## Installation

### Gradle (Kotlin DSL)
```kotlin
repositories {
    maven("https://jitpack.io")
}

dependencies {
    implementation("com.github.R3pentless:commandframework:1.0.0")
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
```

## Usage
```java
@Command(name = "test", permission = "plugin.test")
public class TestCommand {
    
    @Default
    public void execute(Player player) {
        player.sendMessage("Hello!");
    }
    
    @SubCommand("give")
    public void give(Player sender,
                     @Arg("player") Player target,
                     @Arg("item") Material material,
                     @Arg("amount") int amount) {
        // Auto tab-completion!
        target.getInventory().addItem(new ItemStack(material, amount));
    }
}
```

Register:
```java
public class MyPlugin extends JavaPlugin {
    @Override
    public void onEnable() {
        CommandFramework framework = new CommandFramework(this);
        framework.register(new TestCommand());
    }
}
```

## Features

✅ Annotation-based commands  
✅ Auto tab-completion with smart suppliers  
✅ No plugin.yml entries needed  
✅ Built-in parsers (Player, Material, World, etc.)  
✅ Custom argument suppliers  
✅ Cooldown system  
✅ Permission handling  
✅ Range validation  
✅ Optional arguments  

## Documentation

See [Wiki](https://github.com/R3pentless/commandframework/wiki) for full documentation.

## License

MIT License
