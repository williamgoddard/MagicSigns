package me.uniqueimpact.magicsigns.objects;

import java.util.List;

public class MagicSign {

    private final String name;
    private final List<String> setupText;
    private final List<String> newText;
    private final List<String> commands;
    private final String message;
    private final String permission;

    public MagicSign(String name, List<String> setupText, List<String> newText, List<String> commands, String message, String permission) {
        this.name = name;
        this.setupText = setupText;
        this.newText = newText;
        this.commands = commands;
        this.message = message;
        this.permission = permission;
    }

    public String getName() {
        return name;
    }

    public List<String> getSetupText() {
        return setupText;
    }

    public List<String> getNewText() {
        return newText;
    }

    public List<String> getCommands() {
        return commands;
    }

    public String getMessage() {
        return message;
    }

    public String getPermission() {
        return permission;
    }

}
