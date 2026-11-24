package io.github.duckysmacky.guncore.common.config.menus;

import io.github.duckysmacky.guncore.common.config.registry.Registry;

import java.util.List;
import java.util.Optional;

/** Registry of the sections menus group catalog entries by, stored in `config/guncore/menus/` */
public class MenuSectionManager extends Registry<MenuSectionType> {
    public MenuSectionManager() {
        super("menus", "menus/", MenuSectionType.class);
    }

    public List<MenuSectionEntry> getSections(MenuSectionType type) {
        return getEntries(type);
    }

    public Optional<MenuSectionEntry> findSection(MenuSectionType type, String id) {
        return getSections(type).stream().filter(section -> section.getId().equals(id)).findFirst();
    }
}
