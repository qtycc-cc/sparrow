package com.example.sparrow.demo;

import com.example.sparrow.client.annotation.SparrowJsonValue;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Map;

@Data
@Configuration
public class TestProperties {
    @Value("${byte.value}")
    private byte byteValue;
    @Value("${short.value}")
    private short shortValue;
    @Value("${int.value}")
    private int intValue;
    @Value("${long.value}")
    private long longValue;
    @Value("${float.value}")
    private float floatValue;
    @Value("${double.value}")
    private double doubleValue;
    @Value("${char.value}")
    private char charValue;
    @Value("${boolean.value}")
    private boolean booleanValue;

    @Value("${byte.value}")
    private Byte boxedByteValue;
    @Value("${short.value}")
    private Short boxedShortValue;
    @Value("${int.value}")
    private Integer boxedIntValue;
    @Value("${long.value}")
    private Long boxedLongValue;
    @Value("${float.value}")
    private Float boxedFloatValue;
    @Value("${double.value}")
    private Double boxedDoubleValue;
    @Value("${char.value}")
    private Character boxedCharValue;
    @Value("${boolean.value}")
    private Boolean boxedBooleanValue;

    @Value("#{'${byte.list}'.split('#')}")
    private byte[] bytes;
    @Value("#{'${short.list}'.split('#')}")
    private short[] shorts;
    @Value("#{'${int.list}'.split('#')}")
    private int[] ints;
    @Value("#{'${long.list}'.split('#')}")
    private long[] longs;
    @Value("#{'${float.list}'.split('#')}")
    private float[] floats;
    @Value("#{'${double.list}'.split('#')}")
    private double[] doubles;
    @Value("#{'${char.list}'.split('#')}")
    private char[] chars;
    @Value("#{'${boolean.list}'.split('#')}")
    private boolean[] booleans;

    @Value("#{'${byte.list}'.split('#')}")
    private Byte[] boxedBytes;
    @Value("#{'${short.list}'.split('#')}")
    private Short[] boxedShorts;
    @Value("#{'${int.list}'.split('#')}")
    private Integer[] boxedInts;
    @Value("#{'${long.list}'.split('#')}")
    private Long[] boxedLongs;
    @Value("#{'${float.list}'.split('#')}")
    private Float[] boxedFloats;
    @Value("#{'${double.list}'.split('#')}")
    private Double[] boxedDoubles;
    @Value("#{'${char.list}'.split('#')}")
    private Character[] boxedChars;
    @Value("#{'${boolean.list}'.split('#')}")
    private Boolean[] boxedBooleans;

    @Value("#{'${byte.list}'.split('#')}")
    private List<Byte> byteList;
    @Value("#{'${short.list}'.split('#')}")
    private List<Short> shortList;
    @Value("#{'${int.list}'.split('#')}")
    private List<Integer> integerList;
    @Value("#{'${long.list}'.split('#')}")
    private List<Long> longList;
    @Value("#{'${float.list}'.split('#')}")
    private List<Float> floatList;
    @Value("#{'${double.list}'.split('#')}")
    private List<Double> doubleList;
    @Value("#{'${boolean.list}'.split('#')}")
    private List<Boolean> booleanList;
    @Value("#{'${char.list}'.split('#')}")
    private List<Character> characterList;

    @Value("${string.value}")
    private String stringValue;
    @Value("#{'${string.list}'.split('#')}")
    private List<String> stringList;

    @Value("#{${map.value}}")
    private Map<String, String> map;

    @SparrowJsonValue("${person.value}")
    private Person person;
    @SparrowJsonValue("${person.list}")
    private List<Person> persons;
    @SparrowJsonValue("${person.map}")
    private Map<String, Person> personMap;
}
