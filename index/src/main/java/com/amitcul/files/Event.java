package com.amitcul.files;

import java.nio.file.Path;
import java.nio.file.WatchEvent;

public record Event(Path path, WatchEvent<?> event) {}
