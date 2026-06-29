FROM maven:3.9.6-eclipse-temurin-21-alpine AS build
WORKDIR /app

# Copia o pom e baixa as dependências (ajuda a usar o cache do Docker)
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copia o código fonte e gera o arquivo .jar (pulando os testes para agilizar)
COPY src ./src
RUN mvn clean package -DskipTests

# Estágio 2: Imagem final de execução (apenas o JRE para ficar leve)
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Copia o JAR gerado no estágio anterior para a imagem definitiva
COPY --from=build /app/target/*.jar app.jar

# Porta que o seu Auth Service usa
EXPOSE 8083

# Comando para rodar a aplicação
ENTRYPOINT ["java", "-jar", "app.jar"]