# --- Asama 1: Build ---
# Maven ve JDK iceren image ile uygulamayi derle
FROM eclipse-temurin:21-jdk AS builder

WORKDIR /app

# Once sadece pom.xml kopyala — bagimliliklar degismezse bu katman cache'lenir
COPY pom.xml .
COPY .mvn .mvn
COPY mvnw .

# Bagimlilikları indir (kod kopyalanmadan once — Docker layer cache icin)
RUN ./mvnw dependency:go-offline -B

# Kaynak kodunu kopyala ve jar olustur (testleri atla — zaten CI'da calisacak)
COPY src src
RUN ./mvnw package -DskipTests -B

# --- Asama 2: Runtime ---
# Sadece JRE iceren kucuk image — build araclari yok, image boyutu kucuk
FROM eclipse-temurin:21-jre

WORKDIR /app

# Build asamasindan sadece jar'i al
COPY --from=builder /app/target/*.jar app.jar

# Spring Boot'un dinledigi port
EXPOSE 8081

# Container basladiginda calistir
ENTRYPOINT ["java", "-jar", "app.jar"]