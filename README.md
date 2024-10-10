# Índice
- [RestQl](#restql)
- [Adjustments and Improvements](#adjustments-and-improvements)
- [Prerequisites](#prerequisites)
- [Installation](#installation)
- [Usage](#usage)

## RestQl
<p>Inspired by the flexibility of GraphQL, <strong>RestQl</strong> is a library designed to enable dynamic field
selection in REST APIs developed in Java. With simple syntax and minimal configuration, RestQl is an efficient solution
for those looking to improve the flexibility of REST APIs without needing to migrate to GraphQL or redesign an
application from scratch.</p>
RestQl was specifically designed for existing applications, providing an easy way to optimize responses without the
learning curve or complexity of adopting technologies like GraphQL. Currently, it offers native support for 
<strong>Spring Boot</strong>, but its modular architecture makes it easy to adapt to other frameworks in the future.

## Adjustments and Improvements
- [ ] Expand the library to support Quarkus.;
- [ ] Provide an example with Gson serialization.

## Prerequisites
- Java `22`
- Sprint Boot `3.3.4`

## Installation
Depending on your approach, add the following dependencies to your pom.xml:
* Imperative Approach:
```xml
<dependency>
<groupId>io.github.cleverton-heusner</groupId>
<artifactId>restql-core</artifactId>
<version>1.0.3</version>
</dependency>
```
* Declarative Approach:
</br></br>In addition to the dependency above, add the module below:
```xml
<dependency>
<groupId>io.github.cleverton-heusner</groupId>
<artifactId>restql-spring-boot</artifactId>
<version>1.0.0</version>
</dependency>
```

## Usage
* Imperative Approach:
1. Make Spring aware of the ```RestQlQuery``` bean with the following configuration:
```java
    @Configuration
    public class FieldsSelectorConfiguration {

        @Bean
        public RestQlQuery restQlQuery() {
            return new RestQlQuery();
        }
    }
```
2. After injecting the ```RestQlQuery``` class, use the ```select``` method to specify the fields to be filtered, and
the ```from``` method for the entity. Example usage in a controller:
```java
  @RestController
  public class PostController {

      @Autowired
      private RestQlQuery restQlQuery;
    
      @GetMapping("/post")
      public ResponseEntity<Map<String, Object>> getPost(@RequestParam(name = "fields", required = false) final String fields) {
          final Post postMock = Instancio.create(Post.class);
          final Map<String, Object> postMockWithSelectedFields = restQlQuery.select(fields).from(postMock);
        
          return ResponseEntity.ok(postMockWithSelectedFields);
      }
  }
```
3. Execute the command ```mvn spring-boot:run``` to start the application;
4. The fields should be selected via a query parameter. The key should be ```fields```, and the value should be the 
fields separated by **commas**. Nested entities should be separated by **dots**. Below is an example request:</br>
http://localhost:8080/post?fields=id,text,author.id,author.email,author.pet.age,author.pet.name

* Declarative Approach:
1. Refer to step **1** of the imperative approach;
2. Annotate the entity class with ```@RestQl```:
```java
    @RestQl
    public class Post {

        private long id;
        private String title;
        private String text;
        private Author author;
        private List<Comment> comments = new ArrayList<>();

        // Getters and Setters omitted for brevity
    }
```
3. Configure the serialization to read the ```@RestQl``` annotation. This configuration exemplifies it using the library **Jackson**:
```java
@Configuration
public class ObjectMapperConfiguration {

    @Bean
    public ObjectMapper objectMapper() {
        final var mapper = new ObjectMapper();
        mapper.setDefaultTyping(restQlTypeResolverBuilder());
        return mapper;
    }
    
    private StdTypeResolverBuilder restQlTypeResolverBuilder() {
        return new RestQlTypeResolverBuilder(BasicPolymorphicTypeValidator.builder()
                .allowIfBaseType(Object.class)
                .build()
        ).init();
    }
}
```
4. Implement a custom filter to process the response based on the selected fields:
```java   
@Component
public class EntityFieldsFilter implements Filter {

    private final RestQlQuery restQlQuery;
    private final ObjectMapper objectMapper;

    public EntityFieldsFilter(final RestQlQuery restQlQuery, final ObjectMapper objectMapper) {
        this.restQlQuery = restQlQuery;
        this.objectMapper = objectMapper;
    }

    @Override
    public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain) throws IOException, ServletException {
    
        final String fields = request.getParameter(FIELDS);
        final var restQlResponseWrapper = new RestQlResponseWrapper(response, objectMapper);
        chain.doFilter(request, restQlResponseWrapper);
    
        if (!StringUtils.isBlank(fields)) {
            final var entityWithSelectedFields = restQlQuery.select(fields).from(restQlResponseWrapper.readEntity());
            restQlResponseWrapper.writeEntityWithSelectedFields(entityWithSelectedFields);
        }
        else {
            restQlResponseWrapper.writeEntityWithAllFields();
        }
    }
}
```
5. Finally, refer to steps **4** and **5** of the previous approach.
## References
- [Example Project](https://github.com/cleverton-heusner/restql-usage)