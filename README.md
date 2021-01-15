<h1 align="center">
    <span href="">Orange Talent - Desafio ZUP </span>
</h1>

### Sobre o Projeto

- Criar uma API REST que precisará gerar números aleatórios para loteria. Para facilitar na identificação da pessoa, você deverá associar cada número a um e-mail.

- No primeiro passo, precisamos construir um endpoint que receberá o e-mail da pessoa e retornará um objeto de resposta com os números sorteados para a aposta. Você deverá garantir que estas informações estejam gravadas em um banco de dados e devidamente associadas à pessoa.

- Também devemos construir um segundo endpoint para listar todas as apostas de um solicitante, passando seu e-mail como parâmetro, o sistema deverá retornar em ordem de criação todas as suas apostas.

### Iniciando o projeto

Para iniciar o projeto iremos precisar de utilizar o banco de dados para persistir nossas informações, para isso utilizaremos o MySQL e faremos algumas configurações no arquivo **application.yml** para o JPA/Hibernate com algumas informações de acesso ao banco de dados.


```yml
spring:
  application:
    name: lottery
  jpa:
    show-sql: true
    hibernate:
      ddl-auto: update
    properties:
      hibernate:
      dialect: org.hibernate.dialect.MySQL5InnoDBDialect
  datasource:
    url: jdbc:mysql://127.0.0.1:3306/lottery?allowPublicKeyRetrieval=true
&sslMode=DISABLED&useSSL=false&useTimezone=true&serverTimezone=UTC
    username: root
    password: admin
```

Após a configuração do banco de dados, foi realizado a criação das entidades que serão responsáveis por definir a Model e a estruturação da tabela no banco de dados. 

Para facilitar no desenvolvimento do projeto, foi utilizado o **Lombok** que basicamente vai gerar nossos códigos em tempo de compilação. E para que seja utilizado, basta adicionar à dependência no Maven e também instalar um plugin para que a IDE consiga entender quais são os métodos criados pelas anotações e nos dê acesso a eles. E também uma das grandes vantagens da sua utilização é a diminuição da verbosidade das classes e dessa maneira conseguimos poupar tempo e ganhamos produtividade para aspectos mais cruciais de implementação. 

```
- @Entity: Nossa classe Person é uma entidade que será mapeada no nosso banco de dados.
- @Id/@GeneratedValue: O atributo anotado será a primary key da tabela e será gerado automaticamente usando 
a estratégia IDENTITY.
- @NoArgsContructor: Adiciona um construtor vazio.
- @AllArgsContructor: Cria um construtor com todos os atributos.
- @Getter: Cria os getters.
- @Setter: Cria os setters.
- @EqualsAndHashCode: Cria o equals e hashcode.
- @Table: Definição do nome da tabela do SQL (Se torna opcional, quando o nome que será dado é o mesmo da entidade)
- @Column: É uma anotação que podemos personalizar o mapeamento entre o atributo e a coluna do banco de dados.
- @OneToMany: Mapeia a associação no banco de dados.
```

A entidade **Person** foi pensada com base no problema proposto de que a pessoa deve ter seu e-mail associado à sua aposta, ou seja, os tickets. Sendo assim, fazendo o mapeamento de um para muitos. E como os tickets estão associados ao e-mail da pessoa, foi definido que o e-mail não pode ser null e também deve ser único para garantir que não ocorra duplicidade. 

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

A entidade **Ticket** é responsável por gerar um número randômico que será o número referente a aposta. Com isso, também um dos requisitos era que fosse retornado em ordem de criação, para isso foi necessário um atributo de data de criação. E para gerar o número randômico foi criado um método que vai gerar uma sequência aleatória utilizando a classe **Random** do próprio Java. 

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

A interface **PersonRepository** deverá estender da interface **JpaRepository** que vai nos prover os métodos necessários para fazer um CRUD e vai ser disponibilizado em tempo de execução pelo próprio Spring Data JPA. E como teve a necessidade de fazer uma consulta pelo e-mail, podemos fazer isso a partir da assinatura do método, dessa forma o Spring Data entende que deve fazer uma busca pelo e-mail que seja igual ao que foi passado por parâmetro. 


```
@Repository: tem como objeto criar beans para a persistência dos dados, além de capturar excepções 
específicas de persistência.
```

```java
@Repository
public interface PersonRepository extends JpaRepository<Person, Long> {
    
    Optional<Person> findByEmail(String email);
}
```

A classe **PersonRequestDTO** foi pensada com base no requisito de que devemos passar apenas o e-mail da pessoa, para que, fosse retornado somente os números sorteados, devido a isso, foi utilizado o padrão DTO que permite que a gente não exponha o modelo de domínio. 

- Para a estruturação dessa classe foi utilizado anotações do Lombok, Swagger e também do hibernate validator.

```
@ApiModelProperty - Utilizada para controlar definições do nosso modelo para auxiliar na interface UI do Swagger.
@NotEmpty - Verifica se o campo não é nulo e nem vazio.
@Email - Verifica se o campo possui as características de um endereço de e-mail.
@Data - Irá gerar automaticamente ToString, EqualsAndHashCode, Getter, Setter, RequiredArgsConstructor.
```

```java
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PersonRequestDTO {

    @ApiModelProperty(value = "E-mail da pessoa.", required = true, example = "matheus@gmail.com")
    @NotEmpty(message = "Favor informar o e-mail.")
    @Email
    private String email;

}
```

A classe **PersonResponseDTO** será nossa classe que será a resposta da nossa requisição, ou seja, seguindo as especificações quando fosse requisitado um e-mail de uma pessoa, deve-se retornar os números sorteados, portanto essa é a responsabilidade dessa classe que também há algumas anotações do Lombok para auxiliar no desenvolvimento. 

```java
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PersonResponseDTO {

    private List<Ticket> tickets;

}
```

### Mapper 

Para realizarmos o mapeamento de DTOs, foi utilizado o framework MapStruct. É necessário esse mapeamento, devido que, por questões de boa prática não é recomendado expor entidades de domínio, pois, assim podemos evitar ataques maliciosos. E a vantagem de se utilizar dessa ferramenta por mais que seja simples mapear DTOs, quando a aplicação vai se tornando maior, ela vai garantir uma padronização e eventuais erros de mapeamento. 

@Mapper(componentModel = "spring") - passando esse parâmetro, estamos dizendo que será uma interface gerenciada pelo spring, podendo ser feita à injeção de dependências.


```java
@Mapper(componentModel = "spring")
public interface PersonMapper {

    PersonMapper INSTANCE = Mappers.getMapper(PersonMapper.class);

    PersonResponseDTO toDTO(Person person);

    Person to(PersonRequestDTO requestDTO);
}
```

O serviço irá conter dois métodos e para isso foi criado um contrato criando uma interface para que fosse implementada posteriormente.

- O primeiro método **getOrCreate**, basicamente é responsável por criar uma nova pessoa com um ticket, porém, caso for inserido novamente o mesmo e-mail, será apenas criado um novo ticket para aquele determinado usuário.
- O segundo método **findBetByEmail**, é responsável por buscar a pessoa pelo seu e-mail e ordenar seus tickets por ordem de criação e caso não encontre é retornado uma exceção personalizada.

Podemos observar que tanto o retorno e o parâmetro passado são utilizados os DTOs para que não exponha nossa entidade de domínio. 

```java
public interface PersonService {

    PersonResponseDTO getOrCreate(PersonRequestDTO requestDTO);

    PersonResponseDTO findBetByEmail(String email) throws PersonNotFoundException;

}
```

Nosso primeiro método **getOrCreate**, utiliza-se de outro método no primeiro momento que será responsável por verificar se o e-mail passado se encontra na nossa base de dados, caso houver, iremos utilizar de outro método que está de responsabilidade do **TicketService** que irá gerar um novo ticket para esse e-mail, já fazendo a validação para que não seja um ticket repetido para o mesmo e-mail. Caso não for encontrado nenhum e-mail, ele já vai retornar o objeto mapeado que será gerado uma nova pessoa com um novo ticket e por fim, será salvo na base de dados. 

- Dessa forma foi criado um método que é capaz de criar uma nova pessoa com um ticket, ou, caso ela já tenha sido cadastrada previamente, somente é adicionado um ticket para ela.

```java
@Override
    public PersonResponseDTO getOrCreate(PersonRequestDTO requestDTO) {

        final var person = verifyIfIsAlreadyRegistered(requestDTO);

        if(person.getTickets() == null) {
            person.setTickets(List.of(new Ticket()));
        }

        final var saved = this.personRepository.save(person);

        return personMapper.toDTO(saved);
    }
    
     private Person verifyIfIsAlreadyRegistered(PersonRequestDTO requestDTO) {
        return personRepository.findByEmail(requestDTO.getEmail())
                .map(person -> {
                    this.ticketService.verifyIfTicketNumberAlreadyExistsAndCreateNew(person.getTickets());
                    return person;
                }) // get and create ticket
                .orElseGet(() -> personMapper.to(requestDTO)); // quando não existe email
    }
    
```

O segundo método é responsável por chamar o **repository** que irá fazer uma consulta personalizada para buscar o e-mail na base de dados, caso houver, será feito um mapeamento para que seja feita à ordenação por ordem de criação e mapeado para o DTO. Caso, não encontre, será estourado uma exceção personalizada onde indica que não foi encontrado a pessoa com esse e-mail. 

```java
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
```

- Implementação completa: 

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

Classe responsável por retornar uma exceção de forma personalizada quando não encontrar um usuário, para que não retorne uma exceção padrão e fique mais claro para quem está consumindo a API entender o erro que ocorreu. 

```java
@ResponseStatus(HttpStatus.NOT_FOUND)
public class PersonNotFoundException extends Exception {

    public PersonNotFoundException(String email) {
        super(String.format("Person with e-mail %s not found in the system.", email));
    }

}
```

O serviço irá conter um método e para isso foi criado um contrato criando uma interface para que fosse implementada posteriormente.

- O funcionamento do método verifyIfTicketNumberAlreadyExistsAndCreateNew é para garantir um dos requisitos que era assegurar que não houvesse sequência de números iguais para o mesmo e-mail.

```java
public interface TicketService {

    void verifyIfTicketNumberAlreadyExistsAndCreateNew(List<Ticket> tickets);

}
```

```
@Service: Usamos esta anotação para que o framework enxergue nossa classe e indicamos que esta classe é um serviço.
@Slf4j: Abstração de muitos frameworks de log, permitindo independência da implementação concreta de log a ser utilizada.
```

O método criado irá instanciar um novo ticket, o próximo passo será iterar pelo ticket passado por parâmetro que vai receber os tickets armazenados no banco de dados (que será utilizado em outro método) e para garantir que não ocorra duplicidade, iremos verificar se o ticket armazenado é igual ao gerado, caso for, será configurado um novo sorteio, porém, caso esse ticket configurado ainda seja igual à algum ticket previamente cadastrador no banco, será retornado um log de erro informando que o ticket já foi gerado anteriormente.


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

```
@RestController: Indica que este controller por padrão responderá o formato JSON e se trata de um controller REST.
@RequestMapping: Responsável por mapear as urls dos nossos métodos, ou seja, todos os métodos desse 
controller terão como base o "/api/v1/person".
@Api: Utilizada para declarar uma API de recurso do Swagger, somente com essa anotação serão verificadas pelo Swagger.
@PostMapping: Tratam de requisições POST das solicitações HTTP. 
@GetMapping: Tratam de requisições GET das solicitações HTTP.
@RequestBody: Indicamos o objeto PersonRequestDTO que deve ser buscado no corpo da requisição.
@Valid: Indica que o objeto será validado tendo como base as anotações de validação que 
foram atribuídas anteriormente.
@ResponseStatus: Utilizado para especificar o status de resposta HTTP.
@ApiOperation: É utilizado para declarar a operação para o recurso de API e utilizando o *value* podemos fazer 
uma breve descrição.
@PathVariable: Indica que o valor da variável será passado diretamente na URL, não como uma query, após "=?".
```

De acordo com às especificações da API REST deveríamos ter dois *endpoints*, onde o primeiro irá receber o e-mail da pessoa e vai retornar um objeto de resposta com os números sorteados para a aposta e o segundo *endpoint* deve listar todas as apostas de um solicitante, passando o e-mail por parâmetro. Portanto, podemos observar que no primeiro *endpoint* esperamos no corpo da requisição um PersonRequestDTO que contém apenas o e-mail e o retorno sendo PersonResponseDTO que contém a lista de tickets (apostas). No segundo *endpoint* recebemos um e-mail por parâmetro e nosso retorno da requisição também é um PersonResponseDTO que tem toda lista de tickets em ordem de criação. Logo, para realizar essas requisições feitas, estamos utilizando o serviço criado anteriormente. 

```java
@RestController
@RequestMapping("api/v1/person")
@Api(value = "API REST Lottery")
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

- Acredito que toda API necessite ter uma documentação, pensando nisso, foi-se utilizado o Swagger que trás diversas funcionalidades para auxiliar no desenvolvimento. Utilizando com a biblioteca SpringFox, conseguimos gerar especificações de forma simplificada. Além disso, com a utilização do Swagger, temos um módulo UI que permite a interação com a API em **sandbox**, ou seja, podemos testar fazendo requisições nos *endpoints* sem termos que recorrer a outras ferramentas, como por exemplo, o Postman.
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

A imagem abaixo é nossa interface UI disponibilizada pela configuração do Swagger que possibilitara de fazermos requisições de forma simplificada. 

![image](https://user-images.githubusercontent.com/30940498/104593152-066a4e00-564e-11eb-9e79-924c6172257f.png)

Neste momento, estamos fazendo a requisição para criação de uma nova pessoa com seu ticket.

![image](https://user-images.githubusercontent.com/30940498/104605133-dfffdf00-565c-11eb-903d-75b544ff4cc8.png)

Neste ponto, recebemos a resposta da requisição com o ticket criado, associado com à pessoa.

![image](https://user-images.githubusercontent.com/30940498/104600710-1edf6600-5658-11eb-9dfa-2c8dfc1ee75d.png)

Na imagem abaixo, foi realizado uma nova requisição para o mesmo *endpoint*, passando o mesmo e-mail e dessa forma será criado um novo ticket associado ao usuário, garantindo que não seja repetido o número sorteado para o mesmo e-mail.

![image](https://user-images.githubusercontent.com/30940498/104601220-bba20380-5658-11eb-820b-843e16f98f4c.png)

Neste ponto, estamos fazendo a requisição para recuperar os tickets associados a uma pessoa passando seu e-mail como parâmetro.

![image](https://user-images.githubusercontent.com/30940498/104604809-85ff1980-565c-11eb-8e09-d5f57408617b.png)

Na imagem abaixo, foi realizado uma requisição para o segundo *endpoint*, sendo passado o e-mail e tendo como resposta os tickets ordenados por ordem de criação.

![image](https://user-images.githubusercontent.com/30940498/104604521-34ef2580-565c-11eb-9afd-ffdf52640f26.png)

Caso seja passado um e-mail que não se encontra na base de dados, com o tratamento personalizado de exceção será retornado para quem está consumindo a API:

![image](https://user-images.githubusercontent.com/30940498/104656974-f4fd6200-569e-11eb-95fa-cdd39d223a56.png)

### Testes unitários 

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

Para realizarmos testes unitários para o **controller** foi utilizado o **WebTestClient** que por mais que não tenha um ótimo desempenho em relação ao **MockMvc** por não utilizar um contexto fatiado, quando utilizamos dele, temos a possibilidade de realizar um teste exatamente como a aplicação é chamada em produção. E também foi utilizado o Mockito que é uma biblioteca de simulação, que fornece um mecanismo simplificado para adaptar ao comportamento dos mocks. Onde, foi verificado se o PersonService está sendo chamado corretamente exatamente uma vez quando é feito o request para o *endpoint* e também juntamente com a injeção de dependência com o **webTestClient** conseguimos garantir se o status e o corpo da resposta são o esperados.

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

Para realizar os testes unitários no serviço foram utilizadas algumas tecnologias para que esse processo fosse realizado, com isso, contamos com o mockito para simularmos o comportamento e conseguíssemos testar a entrada e saída dos métodos se estão correspondendo como deveria, com auxílio também do hamcrest que possibilita mais legibilidade na hora de escrever asserções e possibilitando uma cobertura de testes unitários para os métodos utilizados no PersonService.

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
    void shouldCreatePersonAndTicketWhenEmailDoesNotExists() {

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

### Implementação no sistema na Web

Pensando na segurança dos dados que serão persistidos e consumidos da nossa base de dados, uma abordagem é prover uma segurança para a aplicação, para isso, podemos prover via JWT, que após o usuário fizer cadastro em um *endpoint* do serviço de autenticação, será provido um token e após isso, quando fizer alguma requisição para nossa API, será enviado via header pelo *Authorization*.  Iremos utilizar o Spring Security que é um framework que vai fornecer a autenticação, autorização e proteção contra ataques maliciosos. Também algo que pode ser feito na aplicação é se utilizar o docker, pois, utilizando dessa ferramenta,  nos permite realizar o empacotamento da nossa aplicação dentro de um container, ou seja, se tornando portável para qualquer outro host, reduzindo drasticamente o tempo de *deploy* da aplicação e até mesmo da infraestrutura, devido que, uma vez “dockerizada”, não há necessidade de ajustes de ambiente para um correto funcionamento da aplicação, então, uma vez configurado, poderá ser replicado quantas vezes quiser. E uma imagem docker, pode ser movida para uma infraestrutura em nuvem, como por exemplo, utilizando o AWS ECR que é um registro de contêiner totalmente gerenciado que facilita o armazenamento, gerenciamento, compartilhamento e a implantação de imagens e artefatos de contêiner.

### Considerações finais

A realização desse desafio foi de extrema importância para colocar diversos conhecimentos em prática e romper diversas barreiras para construir uma API REST. E foi extremamente prazeroso por explicar passo a passo do desenvolvimento da aplicação, com isso, adquirindo ainda mais conhecimento com as ferramentas do ecossistema do Spring Boot. Para resolver o item bônus, para garantir que não houvesse e-mails duplicados foi definido no mapeamento do banco de dados um travamento que o campo seria *unique* e para garantir que não houvesse sequências de números iguais foi criado um método para fazer essa verificação com duas validações para garantir que não esteja sendo gerado sequências duplicadas para o mesmo e-mail.

Repositório com o código completo está disponível no link: https://github.com/mcarneirobug/orange-talents-zup
