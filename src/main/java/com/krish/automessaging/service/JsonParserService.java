package com.krish.automessaging.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface JsonParserService {
    Map<String, List<String>> parsePrivilegesJson() throws IOException;
}
