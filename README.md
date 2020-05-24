[![Release](https://jitpack.io/v/kodgemisi/servlet-url-mapper.svg)](https://jitpack.io/#kodgemisi/servlet-url-mapper)

# Servlet Url Mapper

This small and non-invasive library is intended to be used in servlet-only environments where for some reasons developers are forced to use 
Servlet API directly (like in Atlassian plugin SDK).

In such servlet-only environments, this library helps to handle nested URL structures in one servlet.

_Non-invasive_: You don't need to use `servlet url mapper` in every servlet, you can use only in some of them.

Assume you have a `ProductsServlet` with `/products, /products/*` URL mapping. Then you would handle request like follows:

**Traditional way**

`doGet` method gets messy.

| URL                    | Method | Handling Method        |
|------------------------|--------|------------------------|
| /products              | GET    | ProductsServlet#doGet  |
| /products/13           | GET    | ProductsServlet#doGet  |
| /products/13/campaigns | GET    | ProductsServlet#doGet  |
| /products              | POST   | ProductsServlet#doPost |

<br/>

**Servlet URL Mapper way**

You can use different methods for different URLs in the same servlet. You can even use another Class' method to handle requests.

| URL                    | Method | Handling Method        |
|------------------------|--------|------------------------|
| /products              | GET    | ProductsServlet#list   |
| /products/13           | GET    | ProductsServlet#show   |
| /products/13/campaigns | GET    | CampaignsHelper#show   |
| /products              | POST   | ProductsServlet#create |


## Quick start

1. Add the dependency

You should use [jitpack](https://jitpack.io) to add this library as a dependency for Maven or Gradle (or others).
 
```xml
<repositories>
  <repository>
      <id>jitpack.io</id>
      <url>https://jitpack.io</url>
  </repository>
</repositories>
```
```xml
<dependency>
    <groupId>com.github.kodgemisi</groupId>
    <artifactId>servlet-url-mapper</artifactId>
    <version>1.2.0</version>
</dependency>
```

See https://jitpack.io/#kodgemisi/servlet-url-mapper

2. Extend your servlet from `MappingServlet`

```java
public class MyServlet extends MappingServlet {
	//...
}
```

3. Register your url mappings via `protected urlMappingRegistrar` field from `MappingServlet`. You can do this either in `constructor` or in `init` method.

```java
@WebServlet(urlPatterns = {"/products", "/products/"})
public class MyServlet extends MappingServlet {
  public MyServlet() {
      this.urlMappingRegistrar
      
             // matches GET request to "host/context-root/products"
             .get("/", this::list)

             // matches GET request to "host/context-root/products/all"
             .get("/all", this::list) // note that the same method can be used for multiple url mappings

             // matches GET request to "host/context-root/products/{id}"
             .get("/{id}", this::show)

             // matches POST request to "host/context-root/products"
             .post("/", this::create)

             // matches POST request to "host/context-root/products/{id}/address"
             .post("/{id}/address", AddressHelper::addAddress);
      // and so on...
  }

  // any access modifier can be used
  private void list(HttpServletRequest request, HttpServletResponse response, ServletUrl servletUrl) throws ServletException, IOException {
      // your code...
  }

  private void show(HttpServletRequest request, HttpServletResponse response, ServletUrl servletUrl) throws ServletException, IOException {
      Integer id = servletUrl.variable("id"); // this is parsed from url: /{id}
      // your code...
  }

  // and rest of your methods...
}
```

**Java 8 usage remainder**: Note that (assuming you have an `AddressHelper` class in your project) `AddressHelper::addAddress` usage implies that `addAddress` is a static method. You can use non-static methods by providing an object instead of Class name like `addressHelper::addAddress` assuming `addressHelper` is an object of `AddressHelper` class.

## License and Copyright

© 2017 - 2020 Kod Gemisi Ltd.

All material in this library's repository is copyrighted by [Kod Gemisi Ltd](http://kodgemisi.com/) unless stated otherwise. 

This library is subject to the terms of the Mozilla Public License, v. 2.0.
You can find full license in `license.txt` file or at [http://mozilla.org/MPL/2.0/](http://mozilla.org/MPL/2.0/) adress.

There is also an [easy to understand version](https://tldrlegal.com/license/mozilla-public-license-2.0-(mpl-2)) of the license.
