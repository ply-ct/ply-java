package org.plyct.plyex.util;

import com.beust.jcommander.converters.IParameterSplitter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class CommaSplitter implements IParameterSplitter {
    @Override
    public List<String> split(String value) {
        return Arrays.stream(value.split(",")).map(s -> s.trim()).collect(Collectors.toList());
    }
}

