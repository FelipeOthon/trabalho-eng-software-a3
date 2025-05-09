# trabalho-eng-software-a3
Guia de Configuração e Execução do Projeto: RVJK - Serviços (Spring Boot)

Este guia descreve os passos necessários para configurar o ambiente de desenvolvimento e executar a aplicação "RVJK - Serviços".

1. Pré-requisitos de Software:

Antes de começar, certifique-se de ter os seguintes softwares instalados na sua máquina:

Java Development Kit (JDK):
Versão: Java 21 (ou a versão especificada no pom.xml do projeto, na tag <java.version>).
Você pode baixar o Oracle JDK ou uma distribuição OpenJDK como Adoptium Temurin, Amazon Corretto, etc.
Certifique-se de que a variável de ambiente JAVA_HOME está configurada e que o java e javac estão no seu PATH.
Apache Maven:
Versão: 3.6.x ou superior.
Maven é usado para gerenciar as dependências do projeto e para o build.
Você pode baixá-lo em https://maven.apache.org/download.cgi.
Certifique-se de que o mvn está no seu PATH.
MySQL Server:
Versão: 8.x ou compatível.
Este é o banco de dados que a aplicação utiliza.
Você pode baixá-lo em [URL inválido removido].
Durante a instalação, anote a senha que você definir para o usuário root (ou crie um usuário específico para a aplicação).
Git:
Necessário para clonar o repositório do projeto.
Você pode baixá-lo em https://git-scm.com/downloads.
IDE de Desenvolvimento (Recomendado):
IntelliJ IDEA Ultimate ou Community Edition: Altamente recomendado para desenvolvimento Spring Boot, com excelente integração Maven e Spring.
Eclipse IDE for Java EE Developers (com Spring Tools Suite - STS): Outra opção popular.
Visual Studio Code (com extensões Java e Spring Boot): Uma alternativa leve.
2. Configuração do Banco de Dados MySQL:

Inicie o Servidor MySQL: Certifique-se de que o seu servidor MySQL está em execução.
Crie o Banco de Dados (Schema):
Conecte-se ao seu MySQL usando um cliente de sua preferência (linha de comando, MySQL Workbench, DBeaver, etc.) com um usuário que tenha permissão para criar bancos (normalmente o root).
Execute o seguinte comando SQL:
SQL

CREATE DATABASE servicos CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
(O nome do banco de dados servicos é o padrão configurado no application.properties. Se for diferente no seu projeto, ajuste o comando acima ou o arquivo de propriedades.)
(Opcional, mas Recomendado) Crie um Usuário Específico para a Aplicação:
Ainda no seu cliente MySQL, como root, execute (substitua 'sua_senha_forte' por uma senha segura):
SQL

CREATE USER 'rvjk_user'@'localhost' IDENTIFIED BY 'sua_senha_forte';
GRANT ALL PRIVILEGES ON servicos.* TO 'rvjk_user'@'localhost';
FLUSH PRIVILEGES;
Se você criar este usuário, precisará atualizar as credenciais no arquivo application.properties da aplicação (veja o Passo 4).
3. Obter o Código-Fonte do Projeto:

Clone o Repositório: Se o projeto estiver em um repositório Git (ex: GitHub), clone-o para sua máquina local:
Bash

git clone <URL_DO_REPOSITORIO_GIT>
cd <NOME_DA_PASTA_DO_PROJETO>
Download Manual: Se não estiver usando Git, baixe o código-fonte do projeto e extraia-o para uma pasta no seu computador.
4. Configurar a Aplicação Spring Boot:

Importe o Projeto na sua IDE:
IntelliJ IDEA: "File" > "Open..." e selecione a pasta raiz do projeto (a que contém o arquivo pom.xml). O IntelliJ deve reconhecê-lo como um projeto Maven e baixar as dependências automaticamente.
Eclipse (com STS): "File" > "Import..." > "Maven" > "Existing Maven Projects" e navegue até a pasta raiz do projeto.
Ajuste application.properties:
Navegue até src/main/resources/application.properties.
Verifique e, se necessário, atualize as configurações de conexão com o banco de dados:
Properties

spring.datasource.url=jdbc:mysql://localhost:3306/servicos?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC

# Se você criou um usuário específico para a aplicação:
spring.datasource.username=rvjk_user 
spring.datasource.password=sua_senha_forte 

# Se estiver usando o usuário root do MySQL (ajuste a senha):
# spring.datasource.username=root
# spring.datasource.password=SUA_SENHA_DO_ROOT_MYSQL

# Para a primeira execução em um banco 'servicos' vazio (onde as tabelas ainda não existem),
# use 'create' para que o Hibernate crie as tabelas automaticamente.
spring.jpa.hibernate.ddl-auto=create 

# Para execuções subsequentes, mude para 'update' ou 'validate'.
# spring.jpa.hibernate.ddl-auto=update
Importante sobre spring.jpa.hibernate.ddl-auto:
Use create apenas na primeira vez que rodar a aplicação com um banco de dados servicos vazio para que o Hibernate crie as tabelas.
Após a primeira execução bem-sucedida e a criação das tabelas, pare a aplicação e mude esta propriedade para update ou validate. Se você deixar como create, suas tabelas (e quaisquer dados nelas) serão apagadas e recriadas toda vez que a aplicação iniciar.
5. Construir e Executar a Aplicação:

Construir com Maven (Build):

Sua IDE geralmente faz isso automaticamente ao importar.
Se precisar fazer manualmente, abra um terminal/prompt de comando na pasta raiz do projeto e execute:
Bash

mvn clean install
Isso baixará todas as dependências e compilará o projeto.
Executar a Aplicação:

Via IDE (Recomendado):
Encontre a classe principal da aplicação, que geralmente se chama NomeDoProjetoApplication.java (no seu caso, TrabalhoUdwmjApplication.java) dentro do pacote com.exemplo.trabalho.
Esta classe terá um método public static void main(String[] args).
Clique com o botão direito nesta classe ou no método main e selecione "Run 'NomeDaClasseApplication.main()'".
Via Linha de Comando (após o build com Maven):
Navegue até a pasta raiz do projeto no terminal.
Execute o comando:
Bash

mvn spring-boot:run
Ou, se você gerou um JAR executável (com mvn package):
Bash

java -jar target/nome-do-seu-jar-SNAPSHOT.jar
(O nome do JAR estará na pasta target/ após o mvn package).
Verificar o Console:

Observe o console de saída. Você deverá ver logs do Spring Boot.
Se ddl-auto=create estiver ativo e o banco servicos existir, você verá os comandos SQL CREATE TABLE.
Procure por uma mensagem indicando que a aplicação iniciou com sucesso, como: Tomcat started on port(s): 8080 (http) Started TrabalhoUdwmjApplication in X.XXX seconds
6. Acessar a Aplicação:

Após a aplicação iniciar com sucesso, abra seu navegador web.
Acesse a URL base da aplicação, que por padrão é: http://localhost:8080/
Você deverá ver a página inicial (index.html) da aplicação RVJK - Serviços.
Navegue pelas outras páginas usando os links do menu para testar se os templates estão carregando (ex: cadastro de prestador, cadastro de demanda, painel de prestadores, login de admin).
7. Testar as Funcionalidades:

Após a primeira execução com ddl-auto=create (e após mudar para update), comece a testar as funcionalidades:
Cadastre um novo prestador.
Cadastre uma nova demanda de serviço.
Verifique o painel de prestadores (ele deve começar a listar dados após os cadastros).
Verifique seu banco de dados MySQL para confirmar se os dados estão sendo salvos corretamente nas tabelas.
Solução de Problemas Comuns:

Erro "Access denied for user ...": Verifique o nome de usuário e senha do MySQL no application.properties. Certifique-se de que o usuário tem permissão para acessar o banco servicos do localhost.
Erro "Database 'servicos' unknown/not found": Certifique-se de que você criou o banco de dados servicos no MySQL (Passo 2.2).
Whitelabel Error Page / Erro 404:
Verifique se o MainController.java tem um @GetMapping para a URL que você está tentando acessar.
Verifique se o nome do arquivo HTML retornado pelo controller (ex: return "index";) corresponde exatamente ao nome do arquivo em src/main/resources/templates/ (ex: index.html).
Verifique se há erros de Thymeleaf no console do Spring Boot ao tentar renderizar a página.
Erros de Compilação na IDE: Verifique os imports, nomes de classes e métodos. Certifique-se de que todas as dependências do Maven foram baixadas corretamente.
CSS ou JS não carregam: Verifique os caminhos th:href e th:src nos seus arquivos HTML e certifique-se de que os arquivos estáticos estão na estrutura correta dentro de src/main/resources/static/. Use as ferramentas de desenvolvedor do navegador (F12) para inspecionar erros de rede ou console.