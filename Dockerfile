# 银行信贷额度管控平台 - Dockerfile

# 使用Java 17作为基础镜像
FROM openjdk:17-jdk-slim

# 设置工作目录
WORKDIR /app

# 复制Maven构建文件
COPY pom.xml .

# 复制源代码
COPY src ./src

# 安装Maven（如果基础镜像没有）
RUN apt-get update && apt-get install -y maven && \
    apt-get clean && rm -rf /var/lib/apt/lists/*

# 构建应用
RUN mvn clean package -DskipTests

# 复制构建好的JAR文件
RUN mv target/credit-quota-management-platform-1.0.0.jar app.jar

# 暴露端口
EXPOSE 8080

# 启动应用
ENTRYPOINT ["java", "-jar", "app.jar"]