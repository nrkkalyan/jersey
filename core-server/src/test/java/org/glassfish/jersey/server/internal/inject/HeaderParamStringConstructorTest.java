/*
* DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
*
* Copyright (c) 2010-2012 Oracle and/or its affiliates. All rights reserved.
*
* The contents of this file are subject to the terms of either the GNU
* General Public License Version 2 only ("GPL") or the Common Development
* and Distribution License("CDDL") (collectively, the "License").  You
* may not use this file except in compliance with the License.  You can
* obtain a copy of the License at
* http://glassfish.java.net/public/CDDL+GPL_1_1.html
* or packager/legal/LICENSE.txt.  See the License for the specific
* language governing permissions and limitations under the License.
*
* When distributing the software, include this License Header Notice in each
* file and include the License file at packager/legal/LICENSE.txt.
*
* GPL Classpath Exception:
* Oracle designates this particular file as subject to the "Classpath"
* exception as provided by Oracle in the GPL Version 2 section of the License
* file that accompanied this code.
*
* Modifications:
* If applicable, add the following below the License Header, with the fields
* enclosed by brackets [] replaced by your own identifying information:
* "Portions Copyright [year] [name of copyright owner]"
*
* Contributor(s):
* If you wish your version of this file to be governed by only the CDDL or
* only the GPL Version 2, indicate your decision by adding "[Contributor]
* elects to include this software in this distribution under the [CDDL or GPL
* Version 2] license."  If you don't indicate a single choice of license, a
* recipient has the option to distribute your version of this file under
* either the CDDL, the GPL Version 2 or to extend the choice of license to
* its licensees as provided above.  However, if you add GPL Version 2 code
* and therefore, elected the GPL Version 2 license, then the option applies
* only if the new code is made subject to such option by the copyright
* holder.
*/

package org.glassfish.jersey.server.internal.inject;

import org.glassfish.jersey.message.internal.Requests;
import org.junit.Test;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static org.junit.Assert.assertEquals;

/**
 *
 * @author Paul Sandoz
 * @author Pavel Bucek (pavel.bucek at oracle.com)
 */
public class HeaderParamStringConstructorTest extends AbstractTest {

    @Path("/")
    public static class ResourceString {
        @GET
        public String doGet(
                @HeaderParam("arg1") BigDecimal arg1,
                @HeaderParam("arg2") BigInteger arg2,
                @HeaderParam("arg3") URI arg3) {
            assertEquals("3.145", arg1.toString());
            assertEquals("3145", arg2.toString());
            assertEquals("http://test", arg3.toString());
            return "content";
        }
    }

    @Path("/")
    public static class ResourceStringList {
        @GET
        public String doGetString(@HeaderParam("args") List<BigDecimal> args) {
            assertEquals("3.145", args.get(0).toString());
            assertEquals("2.718", args.get(1).toString());
            assertEquals("1.618", args.get(2).toString());
            return "content";
        }
    }

    @Path("/")
    public static class ResourceStringListEmpty {
        @GET
        public String doGetString(@HeaderParam("args") List<BigDecimal> args) {
            assertEquals(3, args.size());
            assertEquals(null, args.get(0));
            assertEquals(null, args.get(1));
            assertEquals(null, args.get(2));
            return "content";
        }
    }

    @Path("/")
    public static class ResourceStringNullDefault {
        @GET
        public String doGet(
                @HeaderParam("arg1") BigDecimal arg1) {
            assertEquals(null, arg1);
            return "content";
        }
    }

    @Path("/")
    public static class ResourceStringDefault {
        @GET
        public String doGet(
                @HeaderParam("arg1") @DefaultValue("3.145") BigDecimal arg1) {
            assertEquals("3.145", arg1.toString());
            return "content";
        }
    }

    @Path("/")
    public static class ResourceStringDefaultOverride {
        @GET
        public String doGet(
                @HeaderParam("arg1") @DefaultValue("3.145") BigDecimal arg1) {
            assertEquals("2.718", arg1.toString());
            return "content";
        }
    }

    @Path("/")
    public static class ResourceStringListEmptyDefault {
        @GET
        public String doGetString(@HeaderParam("args") List<BigDecimal> args) {
            assertEquals(0, args.size());
            return "content";
        }
    }

    @Path("/")
    public static class ResourceStringListDefault {
        @GET
        public String doGetString(
                @HeaderParam("args") @DefaultValue("3.145") List<BigDecimal> args) {
            assertEquals("3.145", args.get(0).toString());
            return "content";
        }
    }

    @Path("/")
    public static class ResourceStringListDefaultOverride {
        @GET
        public String doGetString(
                @HeaderParam("args") @DefaultValue("3.145") List<BigDecimal> args) {
            assertEquals("2.718", args.get(0).toString());
            return "content";
        }
    }

    @Test
    public void testStringConstructorGet() throws ExecutionException, InterruptedException {
        initiateWebApplication(ResourceString.class);

        assertEquals("content", apply(
                Requests.from("/", "GET").
                        header("arg1", "3.145").
                        header("arg2", "3145").
                        header("arg3", "http://test").
                        build()
        ).readEntity(String.class));
    }

    @Test
    public void testStringConstructorListGet() throws ExecutionException, InterruptedException {
        initiateWebApplication(ResourceStringList.class);

        assertEquals("content", apply(
                Requests.from("/", "GET").
                        accept("application/stringlist").
                        header("args", "3.145").
                        header("args", "2.718").
                        header("args", "1.618").
                        build()
        ).readEntity(String.class));
    }

    @Test
    public void testStringConstructorListEmptyGet() throws ExecutionException, InterruptedException {
        initiateWebApplication(ResourceStringListEmpty.class);

        assertEquals("content", apply(
                Requests.from("/", "GET").
                        accept("application/stringlist").
                        header("args", "").
                        header("args", "").
                        header("args", "").
                        build()
        ).readEntity(String.class));
    }

    @Test
    public void testStringConstructorEmptyDefault() throws ExecutionException, InterruptedException {
        initiateWebApplication(ResourceStringNullDefault.class);

        _test("/");
    }

    @Test
    public void testStringConstructorDefault() throws ExecutionException, InterruptedException {
        initiateWebApplication(ResourceStringDefault.class);

        _test("/");
    }

    @Test
    public void testStringConstructorDefaultOverride() throws ExecutionException, InterruptedException {
        initiateWebApplication(ResourceStringDefault.class);

        assertEquals("content", apply(
                Requests.from("/", "GET").
                        header("args", "2.718").
                        build()
        ).readEntity(String.class));
    }

    @Test
    public void testStringConstructorListNullDefault() throws ExecutionException, InterruptedException {
        initiateWebApplication(ResourceStringListEmptyDefault.class);

        _test("/");
    }

    @Test
    public void testStringConstructorListDefault() throws ExecutionException, InterruptedException {
        initiateWebApplication(ResourceStringListDefault.class);

        _test("/");
    }

    @Test
    public void testStringConstructorListDefaultOverride() throws ExecutionException, InterruptedException {
        initiateWebApplication(ResourceStringListDefaultOverride.class);

        assertEquals("content", apply(
                Requests.from("/", "GET").
                        header("args", "2.718").
                        build()
        ).readEntity(String.class));
    }

    @Test
    public void testBadStringConstructorValue() throws ExecutionException, InterruptedException {
        initiateWebApplication(ResourceString.class);

        final Response response = apply(
                Requests.from("/", "GET").
                        header("arg1", "ABCDEF").
                        header("arg2", "3145").
                        header("arg3", "http://test").
                        build()
        );

        assertEquals(400, response.getStatus());
    }
}
