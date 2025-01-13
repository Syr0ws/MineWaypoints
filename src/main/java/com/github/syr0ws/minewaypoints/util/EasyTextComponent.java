package com.github.syr0ws.minewaypoints.util;

import com.github.syr0ws.craftventory.internal.util.TextUtil;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.*;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.List;

public class EasyTextComponent {

    private String text;
    private String showText;
    private String runCommand;
    private String suggestCommand;
    private String openUrl;
    private boolean bold;
    private boolean italic;
    private boolean underlined;
    private boolean obfuscated;
    private boolean strikethrough;
    private ChatColor color;
    private List<EasyTextComponent> extra;

    public String getText() {
        return this.text;
    }

    public void setText(String text) {

        if(text == null) {
            throw new IllegalArgumentException("text cannot be null");
        }

        this.text = text;
    }

    public String getShowText() {
        return this.showText;
    }

    public void setShowText(String showText) {
        this.showText = showText;
    }

    public String getRunCommand() {
        return this.runCommand;
    }

    public void setRunCommand(String runCommand) {
        this.runCommand = runCommand;
    }

    public String getSuggestCommand() {
        return this.suggestCommand;
    }

    public void setSuggestCommand(String suggestCommand) {
        this.suggestCommand = suggestCommand;
    }

    public String getOpenUrl() {
        return this.openUrl;
    }

    public void setOpenUrl(String openUrl) {
        this.openUrl = openUrl;
    }

    public boolean isBold() {
        return this.bold;
    }

    public void setBold(boolean bold) {
        this.bold = bold;
    }

    public boolean isItalic() {
        return this.italic;
    }

    public void setItalic(boolean italic) {
        this.italic = italic;
    }

    public boolean isUnderlined() {
        return this.underlined;
    }

    public void setUnderlined(boolean underlined) {
        this.underlined = underlined;
    }

    public boolean isObfuscated() {
        return this.obfuscated;
    }

    public void setObfuscated(boolean obfuscated) {
        this.obfuscated = obfuscated;
    }

    public boolean isStrikethrough() {
        return this.strikethrough;
    }

    public void setStrikethrough(boolean strikethrough) {
        this.strikethrough = strikethrough;
    }

    public ChatColor getColor() {
        return this.color;
    }

    public void setColor(ChatColor color) {
        this.color = color;
    }

    public List<EasyTextComponent> getExtra() {
        return this.extra;
    }

    public TextComponent toTextComponent() {

        TextComponent component = new TextComponent(TextUtil.parseColors(this.text));

        if(this.showText != null) {
            BaseComponent[] base = new ComponentBuilder(TextUtil.parseColors(this.showText)).create();
            component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, base));
        }

        if(this.runCommand != null) {
            component.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/" + this.runCommand));
        }

        if(this.suggestCommand != null) {
            component.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/" + this.suggestCommand));
        }

        if(this.openUrl != null) {
            component.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, this.openUrl));
        }

        if(this.color != null) {
            component.setColor(this.color);
        }

        component.setBold(this.bold);
        component.setItalic(this.italic);
        component.setUnderlined(this.underlined);
        component.setObfuscated(this.obfuscated);
        component.setStrikethrough(this.strikethrough);

        this.extra.forEach(extra -> component.addExtra(extra.toTextComponent()));

        return component;
    }

    public static EasyTextComponent fromYaml(ConfigurationSection section) {

        EasyTextComponent.Builder builder = new EasyTextComponent.Builder();

        if(!section.contains("text")) {
            throw new IllegalArgumentException(String.format("Property '%s.text' not found", section.getCurrentPath()));
        }

        builder.text(section.getString("text"));

        if(section.contains("show-text"))
            builder.showText(section.getString("show-text"));

        if(section.contains("color"))
            builder.color(ChatColor.of(section.getString("color")));

        if(section.contains("suggest-command"))
            builder.suggestCommand(section.getString("suggest-command"));

        if(section.contains("run-command"))
            builder.runCommand(section.getString("run-command"));

        if(section.contains("open-url"))
            builder.openUrl(section.getString("open-url"));

        builder.bold(section.contains("bold") && section.getBoolean("bold"));
        builder.italic(section.contains("italic") && section.getBoolean("italic"));
        builder.obfuscated(section.contains("obfuscated") && section.getBoolean("obfuscated"));
        builder.strikethrough(section.contains("strikethrough") && section.getBoolean("strikethrough"));

        List<EasyTextComponent> extra = extraFromYaml(section);
        builder.extra(extra);

        return builder.build();
    }

    private static List<EasyTextComponent> extraFromYaml(ConfigurationSection section) {

        List<EasyTextComponent> extra = new ArrayList<>();

        if(section.isConfigurationSection("extra")) {

            for(String key : section.getConfigurationSection("extra").getKeys(false)) {
                ConfigurationSection extraSection = section.getConfigurationSection("extra." + key);
                extra.add(EasyTextComponent.fromYaml(extraSection));
            }
        }

        return extra;
    }

    public static class Builder {

        private String text;
        private String showText;
        private String runCommand;
        private String suggestCommand;
        private String openUrl;
        private boolean bold;
        private boolean italic;
        private boolean underlined;
        private boolean obfuscated;
        private boolean strikethrough;
        private ChatColor color;
        private List<EasyTextComponent> extra;

        public Builder text(String text) {
            this.text = text;
            return this;
        }

        public Builder showText(String showText) {
            this.showText = showText;
            return this;
        }

        public Builder runCommand(String runCommand) {
            this.runCommand = runCommand;
            return this;
        }

        public Builder suggestCommand(String suggestCommand) {
            this.suggestCommand = suggestCommand;
            return this;
        }

        public Builder openUrl(String openUrl) {
            this.openUrl = openUrl;
            return this;
        }

        public Builder bold(boolean bold) {
            this.bold = bold;
            return this;
        }

        public Builder italic(boolean italic) {
            this.italic = italic;
            return this;
        }

        public Builder underlined(boolean underlined) {
            this.underlined = underlined;
            return this;
        }

        public Builder obfuscated(boolean obfuscated) {
            this.obfuscated = obfuscated;
            return this;
        }

        public Builder strikethrough(boolean strikethrough) {
            this.strikethrough = strikethrough;
            return this;
        }

        public Builder color(ChatColor color) {
            this.color = color;
            return this;
        }

        public Builder extra(List<EasyTextComponent> extra) {
            this.extra = extra;
            return this;
        }

        public EasyTextComponent build() {

            if(this.text == null) {
                throw new IllegalArgumentException("text property cannot be null");
            }

            EasyTextComponent component = new EasyTextComponent();

            component.text = this.text;
            component.showText = this.showText;
            component.runCommand = this.runCommand;
            component.suggestCommand = this.suggestCommand;
            component.openUrl = this.openUrl;
            component.bold = this.bold;
            component.italic = this.italic;
            component.underlined = this.underlined;
            component.obfuscated = this.obfuscated;
            component.strikethrough = this.strikethrough;
            component.color = this.color;
            component.extra = this.extra;

            return component;
        }
    }

}
