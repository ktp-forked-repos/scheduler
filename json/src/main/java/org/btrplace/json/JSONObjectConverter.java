/*
 * Copyright (c) 2016 University Nice Sophia Antipolis
 *
 * This file is part of btrplace.
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.btrplace.json;

import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;

import java.io.Reader;
import java.io.StringReader;

/**
 * Specify a converter between a JSON formatted message and a object.
 *
 * @author Fabien Hermenier
 */
public interface JSONObjectConverter<E> {

    /**
     * Un-serialize an object.
     *
     * @param in the json object
     * @return the conversion result
     * @throws JSONConverterException if an error occurred while converting the object
     */
    E fromJSON(JSONObject in) throws JSONConverterException;

    /**
     * Serialize an object.
     *
     * @param e the object to serialize
     * @return the conversion result
     * @throws JSONConverterException if an error occurred while converting the object
     */
    JSONObject toJSON(E e) throws JSONConverterException;

    /**
     * Un-serialize an object from a string.
     *
     * @param buf the string to parse
     * @return the resulting object
     * @throws JSONConverterException if the stream cannot be parsed
     */
    default E fromJSON(String buf) throws JSONConverterException {
        try (StringReader in = new StringReader(buf)) {
            return fromJSON(in);
        }
    }

    /**
     * Un-serialize an object from a stream.
     * The stream must be close afterward
     *
     * @param r the stream to read
     * @return the resulting object
     * @throws JSONConverterException if the stream cannot be parsed
     */
    default E fromJSON(Reader r) throws JSONConverterException {
        try {
            JSONParser p = new JSONParser(JSONParser.MODE_RFC4627);
            Object o = p.parse(r);
            if (!(o instanceof JSONObject)) {
                throw new JSONConverterException("Unable to parse a JSON object");
            }
            return fromJSON((JSONObject) o);
        } catch (ParseException ex) {
            throw new JSONConverterException(ex);
        }
    }

    /**
     * Serialize an object to a string.
     *
     * @param o the object to serialize
     * @return the JSON message
     * @throws JSONConverterException if an error occurred while converting the object
     */
    default String toJSONString(E o) throws JSONConverterException {
        return toJSON(o).toJSONString();
    }
}
