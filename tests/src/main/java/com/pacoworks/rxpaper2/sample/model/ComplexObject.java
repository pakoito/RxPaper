
/*
 * The MIT License (MIT)
 * Copyright (c) 2016 pakoito & 2015 César Ferreira
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation the
 * rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit  persons to whom the Software
 * is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.pacoworks.rxpaper2.sample.model;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Sample bean
 *
 * @author francisco.estevez
 */
public class ComplexObject {
    public static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private int value;

    private String importantValue;

    private List<String> clientList = new ArrayList<>();

    private Map<String, String> clientAddresses = new HashMap<>();

    public ComplexObject() {
    }

    private ComplexObject(Builder builder) {
        setValue(builder.value);
        setImportantValue(builder.importantValue);
        setClientList(builder.clientList);
        setClientAddresses(builder.clientAddresses);
    }

    public static ComplexObject random() {
        return new ComplexObject(
                new Builder().value((int)(Math.random() * 300)).importantValue(randomString())
                        .clientList(Arrays.asList(randomString(), randomString()))
                        .clientAddresses(new HashMap<String, String>() {
                            {
                                final double random = Math.random();
                                for (int i = 0; i < random * 50; i++) {
                                    put(randomString(), randomString());
                                }
                            }
                        }));
    }

    private static String randomString() {
        return new BigInteger(130, SECURE_RANDOM).toString(32);
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public String getImportantValue() {
        return importantValue;
    }

    public void setImportantValue(String importantValue) {
        this.importantValue = importantValue;
    }

    public List<String> getClientList() {
        return clientList;
    }

    public void setClientList(List<String> clientList) {
        this.clientList = new ArrayList<>(clientList);
    }

    public Map<String, String> getClientAddresses() {
        return clientAddresses;
    }

    public void setClientAddresses(Map<String, String> clientAddresses) {
        this.clientAddresses = new HashMap<>(clientAddresses);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        ComplexObject that = (ComplexObject)o;
        if (value != that.value)
            return false;
        if (importantValue != null ? !importantValue.equals(that.importantValue)
                : that.importantValue != null)
            return false;
        if (clientList != null ? !clientList.equals(that.clientList) : that.clientList != null)
            return false;
        return clientAddresses != null ? clientAddresses.equals(that.clientAddresses)
                : that.clientAddresses == null;
    }

    @Override
    public int hashCode() {
        int result = value;
        result = 31 * result + (importantValue != null ? importantValue.hashCode() : 0);
        result = 31 * result + (clientList != null ? clientList.hashCode() : 0);
        result = 31 * result + (clientAddresses != null ? clientAddresses.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ComplexObjectDto{" + "value=" + value + ", importantValue='" + importantValue + '\''
                + ", clientList=" + clientList + ", clientAddresses=" + clientAddresses + '}';
    }

    public static final class Builder {
        private int value;

        private String importantValue;

        private List<String> clientList;

        private Map<String, String> clientAddresses;

        public Builder() {
        }

        public Builder value(int val) {
            value = val;
            return this;
        }

        public Builder importantValue(String val) {
            importantValue = val;
            return this;
        }

        public Builder clientList(List<String> val) {
            clientList = val;
            return this;
        }

        public Builder clientAddresses(Map<String, String> val) {
            clientAddresses = val;
            return this;
        }

        public ComplexObject build() {
            return new ComplexObject(this);
        }
    }
}
