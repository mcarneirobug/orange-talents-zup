<h1 align="center">
    <span href="">Orange Talent - Desafio ZUP :rocket: </span>
</h1>

### 💻 Sobre o Projeto

- Criar uma API REST que precisará gerar números aleatórios para loteria. Para facilitar na identificação da pessoa, você deverá associar cada número a um e-mail.

- No primeiro passo, precisamos construir um endpoint que receberá o e-mail da pessoa e retornará um objeto de resposta com os números sorteados para a aposta. Você deverá garantir que estas informações estejam gravadas em um banco de dados e devidamente associadas à pessoa.

- Também devemos construir um segundo endpoint para listar todas as apostas de um solicitante, passando seu e-mail como parâmetro, o sistema deverá retornar em ordem de criação todas as suas apostas.

### O que deve ter 

- Explique quais as tecnologias do mundo Spring você usaria.
- Conte qual o papel das tecnologias escolhidas e quais benefícios elas trazem para a implementação do código.
- Diga quais classes seriam criadas nesse processo e traga trechos autorais explicando a construção de cada classe do código.
- Explique as etapas do processo de construção do seu código e como faria a implementação do sistema na Web.

### Item Bônus

- Se ficou fácil, considere que você também precisa explicar como faria para proteger a aplicação de e-mails duplicados e sequências de números iguais para o mesmo email.

### 🛠 Iniciando o projeto

Para iniciar o projeto iremos precisar de utilizar o banco de dados para persistir nossas informações, para isso utilizaremos o MySQL e faremos algumas configurações no arquivo: application.yml

Explicação sobre o banco de dados (...)

```yml
spring:
  application:
    name: lottery
  jpa:
    show-sql: false
    hibernate:
      ddl-auto: update
    properties:
      hibernate:
      dialect: org.hibernate.dialect.MySQL5InnoDBDialect
  jmx:
    enabled: false
  datasource:
    url: jdbc:mysql://127.0.0.1:3306/lottery?allowPublicKeyRetrieval=true&sslMode=DISABLED&useSSL=false&useTimezone=true&serverTimezone=UTC
    username: root
    password: admin
```

Explicação sobre o modelo Person citando o Lombok e anotações (...)

```java
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)

@Entity
@Table(catalog = "lottery", name = "person")
public class Person {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @OneToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE})
    private List<Ticket> tickets;

}
```

Explicação sobre o modelo Ticket citando o Lombok e anotações (...)

```java
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)

@Entity
@Table(catalog = "lottery", name = "ticket")
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "random_number", nullable = false)
    private Integer randomNumber = generatedSequence();

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    private Integer generatedSequence() {
        Random code = new Random();
        return code.nextInt(10000000 - 1);
    }
```

Explicação sobre PersonRepository, anotações (...)

```java
@Repository
public interface PersonRepository extends JpaRepository<Person, Long> {
    @Query(value = "FROM Person p "
            + "WHERE p.email = :email"
    )
    Optional<Person> findByEmail(@Param("email") String email);
}
```

Explicação sobre PersonRequestDTO, anotações (...)

```java
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PersonRequestDTO {

    @ApiModelProperty(value = "E-mail da pessoa.", required = true)
    @NotEmpty(message = "Favor informar o e-mail.")
    @Email
    private String email;

}
```

Explicação sobre PersonResponseDTO, anotações (...)

```java
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PersonResponseDTO {

    private List<Ticket> tickets;

}
```

Explicação sobre Mapper, anotações (...)

```java
@Mapper(componentModel = "spring")
public interface PersonMapper {

    PersonMapper INSTANCE = Mappers.getMapper(PersonMapper.class);

    PersonResponseDTO toDTO(Person person);

    Person to(PersonRequestDTO requestDTO);
}
```

Explicação sobre interface PersonService (...)

```java
public interface PersonService {

    PersonResponseDTO getOrCreate(PersonRequestDTO requestDTO);

    PersonResponseDTO findBetByEmail(String email) throws PersonNotFoundException;

}
```

Explicação sobre serviço PersonServiceImpl (...)

```java
@Service
public class PersonServiceImpl implements PersonService {

    private final PersonRepository personRepository;

    private final TicketService ticketService;

    private final PersonMapper personMapper = PersonMapper.INSTANCE;

    public PersonServiceImpl(PersonRepository personRepository, TicketService ticketService) {
        this.personRepository = personRepository;
        this.ticketService = ticketService;
    }

    @Override
    public PersonResponseDTO getOrCreate(PersonRequestDTO requestDTO) {

        final var person = verifyIfIsAlreadyRegistered(requestDTO);

        if(person.getTickets() == null) {
            person.setTickets(List.of(new Ticket()));
        }

        final var saved = this.personRepository.save(person);

        return personMapper.toDTO(saved);
    }

    @Override
    public PersonResponseDTO findBetByEmail(String email) throws PersonNotFoundException {
        return this.personRepository.findByEmail(email)
                .map(person -> {
                    person.getTickets().sort(Comparator.comparing(Ticket::getCreatedAt));
                    return person;
                })
                .map(personMapper::toDTO)
                .orElseThrow(() -> new PersonNotFoundException(email));
    }

    private Person verifyIfIsAlreadyRegistered(PersonRequestDTO requestDTO) {
        return personRepository.findByEmail(requestDTO.getEmail())
                .map(person -> {
                    this.ticketService.verifyIfTicketNumberAlreadyExistsAndCreateNew(person.getTickets());
                    return person;
                }) // get and create ticket
                .orElseGet(() -> personMapper.to(requestDTO)); // quando não existe email
    }
}
```

Explicação sobre exception custom (...)

```java
@ResponseStatus(HttpStatus.NOT_FOUND)
public class PersonNotFoundException extends Exception {

    public PersonNotFoundException(String email) {
        super(String.format("Person with e-mail %s not found in the system.", email));
    }

}
```

Explicação sobre TicketService interface (...)

```java
public interface TicketService {

    void verifyIfTicketNumberAlreadyExistsAndCreateNew(List<Ticket> tickets);

}
```

Explicação sobre TicketServiceImpl, anotações (...)

```java
@Service
@Slf4j
public class TicketServiceImpl implements TicketService {

    private final TicketRepository ticketRepository;

    public TicketServiceImpl(TicketRepository ticketRepository) {
        this.ticketRepository = ticketRepository;
    }

    @Override
    public void verifyIfTicketNumberAlreadyExistsAndCreateNew(List<Ticket> tickets) {

        final var ticket = new Ticket();

        tickets.forEach(t -> {
            if(t.getRandomNumber().equals(ticket.getRandomNumber())) {
                ticket.setRandomNumber(new Random().nextInt(10000000 - 1));
            } else if (ticket.getRandomNumber().equals(t.getRandomNumber())) {
                log.error("Error: Ticket gerado já gerado previamente.");
            }
        });
        tickets.add(ticket);
    }
}
```

Explicação sobre PersonController, anotações, implicações ao se utilizar o CrossOrigin * (...)

```java
@RestController
@RequestMapping("api/v1/person")
@Api(value = "API REST Lottery")
@CrossOrigin(origins = "*")
public class PersonController {

    private final PersonService personService;

    public PersonController(PersonService personService) {
        this.personService = personService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @ApiOperation(value = "Cria uma nova pessoa com uma aposta ou recupera a pessoa e cria uma nova aposta.")
    public PersonResponseDTO createPersonAndBet(@RequestBody @Valid PersonRequestDTO requestDTO) {
        return this.personService.getOrCreate(requestDTO);
    }

    @GetMapping("/{email}")
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "Lista todas as apostas em ordem de criação de uma determinada pessoa.")
    public PersonResponseDTO findBetByEmail(@PathVariable("email") String email) throws PersonNotFoundException {
        return this.personService.findBetByEmail(email);
    }

}
```

### Swagger

- Acredito que toda API necessite ter uma documentação, pensando nisso, foi-se utilizado o Swagger que trás diversas funcionalidades para auxiliar no desenvolvimento. Utilizando com a biblioteca SpringFox, conseguimos gerar especificações de forma simplificada. Além disso, com a utilização do Swagger, temos um módulo UI que permite a interação com a API em sandbox, ou seja, podemos testar fazendo requisições nos endpoints sem termos que recorrer a outras ferramentas, como por exemplo, o Postman.
- Basicamente a classe abaixo é de configuração onde estamos habilitando o uso do Swagger e através do Docket definido como nosso Bean, nos permite configurar aspecto dos endpoints expostos por ele.

```java
@Configuration
@EnableSwagger2
public class SwaggerConfig {

    private static final String API_TITLE = "Lottery API";
    private static final String API_DESCRIPTION = "REST API for lottery";
    private static final String CONTACT_NAME = "Matheus Carneiro";
    private static final String CONTACT_GITHUB = "https://github.com/mcarneirobug";
    private static final String CONTACT_EMAIL = "mccarneiro_@hotmail.com";

    @Bean
    public Docket lotteryApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(basePackage("com.orange.talent.api"))
                .paths(any())
                .build()
                .apiInfo(buildApiInfo());
    }

    private ApiInfo buildApiInfo() {
        return new ApiInfoBuilder()
                .title(API_TITLE)
                .description(API_DESCRIPTION)
                .version("1.0.0")
                .contact(new Contact(CONTACT_NAME, CONTACT_GITHUB, CONTACT_EMAIL))
                .build();
    }

}
```

### :hammer: Testes unitários 

Para realizarmos nossos testes unitários em nosso Service e Controller precisamos de ter o objeto mock para simularmos se está funcionando e capturando nossas validações. Para isso, foi necessário à criação de duas classes, sendo elas PersonUtil e TicketUtils que basicamente irão fornecer os objetos mockados para testarmos.


```java
public class PersonUtils {

    private static final long PERSON_ID = 1L;
    private static final String EMAIL = "matheus@gmail.com";

    public static PersonResponseDTO generatePersonResponseDTO() {
        final var personResponseDTO = new PersonResponseDTO();

        personResponseDTO.setTickets(Collections.singletonList(TicketUtils.generateTicket()));

        return personResponseDTO;
    }

    public static PersonRequestDTO generatePersonRequestDTO() {
        final var personRequestDTO = new PersonRequestDTO();

        personRequestDTO.setEmail(EMAIL);

        return personRequestDTO;
    }

    public static Person generatePerson() {
        final var person = new Person();

        person.setId(PERSON_ID);
        person.setEmail(EMAIL);
        person.setTickets(Collections.singletonList(TicketUtils.generateTicket()));

        return person;
    }
}
```

```java
public class TicketUtils {

    public static Ticket generateTicket() {
        final var ticket = new Ticket();

        ticket.setId(1L);
        ticket.setRandomNumber(1);
        ticket.setCreatedAt(LocalDateTime.MIN);

        return ticket;
    }

}
```

Explicação sobre anotações, PersonControllerTest, testes, mockito, webTestClient (...)

```java
@SpringBootTest(
        classes = ApiApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@AutoConfigureWebTestClient
public class PersonControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private PersonService personService;


    @Test
    void shouldCreatedPersonAndBetWhenPersonIsInformed() {

        final var personResponseDTO = PersonUtils.generatePersonResponseDTO();
        final var personRequestDTO = PersonUtils.generatePersonRequestDTO();

        when(this.personService.getOrCreate(any(PersonRequestDTO.class))).thenReturn(personResponseDTO);

        webTestClient
                .post()
                .uri("api/v1/person")
                .bodyValue(personRequestDTO)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(PersonResponseDTO.class)
                .isEqualTo(personResponseDTO);

        verify(this.personService, times(1)).getOrCreate(any(PersonRequestDTO.class));
    }

    @Test
    void shouldFindBetByEmailWhenEmailIsInformed() throws PersonNotFoundException {

        final var personResponseDTO = PersonUtils.generatePersonResponseDTO();

        when(this.personService.findBetByEmail(anyString())).thenReturn(personResponseDTO);

        webTestClient
                .get()
                .uri("api/v1/person" + "/{email}", "matheus@gmail.com")
                .exchange()
                .expectStatus().isOk()
                .expectBody(PersonResponseDTO.class)
                .isEqualTo(personResponseDTO);

        verify(this.personService, times(1)).findBetByEmail(anyString());
    }

}
```

Explicação sobre anotações, PersonServiceTest, testes, mockito, hamcrest, junit (...)

```java
@SpringBootTest(
        classes = ApiApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
public class PersonServiceTest {

    @MockBean
    private PersonRepository personRepository;

    @MockBean
    private TicketService ticketService;

    @Autowired
    private PersonService personService;

    @Test
    void shouldCreatePersonAndBetWhenPersonIsInformed() {
        // given
        final var person = PersonUtils.generatePerson();
        final var personResponseDTO = PersonUtils.generatePersonResponseDTO();
        final var personRequestDTO = PersonUtils.generatePersonRequestDTO();

        // when
        when(this.personRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(this.personRepository.save(any(Person.class))).thenReturn(person);

        // then
        final var personCreatedDTO = this.personService.getOrCreate(personRequestDTO);

        assertNotNull(personCreatedDTO);
        assertThat(personCreatedDTO, is(equalTo(personResponseDTO)));

        verify(this.personRepository, times(1)).findByEmail(anyString());
        verify(this.personRepository, times(1)).save(any(Person.class));
    }

    @Test
    void shouldFindBetByEmailWhenEmailIsInformed() throws PersonNotFoundException {

        final var person = PersonUtils.generatePerson();
        final var personResponseDTO = PersonUtils.generatePersonResponseDTO();

        when(this.personRepository.findByEmail(anyString())).thenReturn(Optional.of(person));

        final var foundPersonDTO = this.personService.findBetByEmail(person.getEmail());

        assertNotNull(foundPersonDTO);
        assertThat(foundPersonDTO, is(equalTo(personResponseDTO)));

        verify(this.personRepository, times(1)).findByEmail(anyString());
    }

    @Test
    void shouldFindBetByEmailThenAnExceptionShouldBeThrown() {

        when(this.personRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        final var exception = assertThrows(PersonNotFoundException.class,
                () -> this.personService.findBetByEmail("matheus@gmail.com"),
                "Deve retornar um PersonNotFoundException!");

        assertNotNull(exception);
        assertTrue(exception.getMessage().contains("Person with e-mail matheus@gmail.com not found in the system."));

        verify(this.personRepository, times(1)).findByEmail(anyString());
    }

    @Test
    void shouldVerifyIfTicketNumberAlreadyExists() {

        final var person = PersonUtils.generatePerson();
        final var personRequestDTO = PersonUtils.generatePersonRequestDTO();

        when(this.personRepository.findByEmail(anyString())).thenReturn(Optional.of(person));
        doNothing().when(this.ticketService).verifyIfTicketNumberAlreadyExistsAndCreateNew(anyList());

        final var personFound = this.personService.getOrCreate(personRequestDTO);

        verify(this.personRepository, times(1)).findByEmail(anyString());
        verify(this.ticketService, times(1)).verifyIfTicketNumberAlreadyExistsAndCreateNew(anyList());
    }

    @Test
    void shouldCreatePersonAndTicketWhenEmailDoesExists() {

        final var person = PersonUtils.generatePerson();
        final var personRequestDTO = PersonUtils.generatePersonRequestDTO();
        person.setTickets(null);

        when(this.personRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(this.personRepository.save(any(Person.class))).thenReturn(person);

        final var response = this.personService.getOrCreate(personRequestDTO);

        assertNotNull(response);

        verify(this.personRepository, times(1)).findByEmail(anyString());
    }
}
```

### Considerações finais
