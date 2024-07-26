package com.keriko.echorpc.serializer;

import com.caucho.hessian.io.HessianInput;
import com.caucho.hessian.io.HessianOutput;
import com.keriko.echorpc.model.RpcResponse;
import lombok.Data;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;
import java.io.IOException;
import java.io.Serializable;

import static org.junit.Assert.*;


@RunWith(MockitoJUnitRunner.class)
public class HessianSerializerTest {

    @InjectMocks
    private HessianSerializer serializer;

    private static final String TEST_STRING = "Hello, World!";
    private static final int TEST_INT = 42;

    @Data
    static class TestPerson implements Serializable {
        private String name;
        private int age;
    }

    @Before
    public void setUp() {
        serializer = new HessianSerializer();
    }

    @Test
    public void serialize_StringInput_ShouldReturnSerializedBytes() throws IOException {
        byte[] serialized = serializer.serialize(TEST_STRING);
        assertNotNull(serialized);
        assertTrue(serialized.length > 0);
    }

    @Test
    public void deserialize_ValidSerializedString_ShouldReturnDeserializedString() throws IOException {
        byte[] serialized = serializer.serialize(TEST_STRING);
        String deserialized = serializer.deserialize(serialized, String.class);
        assertEquals(TEST_STRING, deserialized);
    }

    @Test
    public void serialize_IntegerInput_ShouldReturnSerializedBytes() throws IOException {
        byte[] serialized = serializer.serialize(TEST_INT);
        assertNotNull(serialized);
        assertTrue(serialized.length > 0);
    }

    @Test
    public void deserialize_ValidSerializedInteger_ShouldReturnDeserializedInteger() throws IOException {
        byte[] serialized = serializer.serialize(TEST_INT);
        Integer deserialized = serializer.deserialize(serialized, Integer.class);
        assertEquals(TEST_INT, deserialized.intValue());
    }

    @Test(expected = IOException.class)
    public void deserialize_InvalidBytes_ShouldThrowIOException() throws IOException {
        byte[] invalidBytes = new byte[]{0, 1, 2, 3};
        serializer.deserialize(invalidBytes, String.class);
    }

    @Test
    public void serializeAndDeserialize_CustomObject_ShouldReturnEqualObject() {
        try {
            HessianSerializer serializer = new HessianSerializer();

            // 示例对象
            TestPerson person = new TestPerson();
            person.setName("John");
            person.setAge(30);

            // 序列化
            byte[] serializedData = serializer.serialize(person);
            System.out.println("Serialized Data: " + serializedData);

            // 反序列化
            TestPerson deserializedPerson = serializer.deserialize(serializedData, TestPerson.class);
            System.out.println("Deserialized Person: " + deserializedPerson);

            // 验证反序列化对象是否与原对象相等
            assertEquals(person.getName(), deserializedPerson.getName());
            assertEquals(person.getAge(), deserializedPerson.getAge());
        } catch (IOException e) {
            e.printStackTrace();
            fail("IOException occurred: " + e.getMessage());
        }
    }

    @Test
    public void serializeAndDeserialize_RpcResponse_ShouldReturnEqualObject() {
        try {
            HessianSerializer serializer = new HessianSerializer();

            // 示例对象
            RpcResponse rpcResponse = new RpcResponse();
            rpcResponse.setData("testMethod");
            rpcResponse.setDataType(String.class);
            rpcResponse.setException(new Exception("IOE"));
            rpcResponse.setMessage("testMessage");


            // 序列化
            byte[] serializedData = serializer.serialize(rpcResponse);
            //System.out.println("Serialized Data: " + serializedData);

            // 反序列化
            RpcResponse deserializedPerson = serializer.deserialize(serializedData, RpcResponse.class);
            //System.out.println("Deserialized Person: " + deserializedPerson);

            // 验证反序列化对象是否与原对象相等
            assertEquals(rpcResponse.getData(), deserializedPerson.getData());
            assertEquals(rpcResponse.getDataType(), deserializedPerson.getDataType());
            //assertEquals(rpcResponse.getException(), deserializedPerson.getException());
            assertEquals(rpcResponse.getMessage(), deserializedPerson.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
            fail("IOException occurred: " + e.getMessage());
        }
    }
}