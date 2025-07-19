package org.example;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class WeaponLoader {
    public WeaponClass loadWeapon(String filePath) throws IOException {
        Yaml yaml = new Yaml();

        try (InputStream inputStream = new FileInputStream(filePath)) {
            return yaml.loadAs(inputStream, WeaponClass.class);
        }
    }
}
