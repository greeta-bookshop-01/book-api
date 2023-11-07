docker-compose down

mvn clean install

cd catalog-service
mvn spring-boot:build-image -DskipTests \
  -Dspring-boot.build-image.imageName=catalog-service

cd ../order-service
mvn spring-boot:build-image -DskipTests \
  -Dspring-boot.build-image.imageName=order-service

cd ../dispatcher-service
mvn spring-boot:build-image -DskipTests \
  -Dspring-boot.build-image.imageName=dispatcher-service

cd ../gateway-service
mvn spring-boot:build-image -DskipTests \
  -Dspring-boot.build-image.imageName=gateway-service

cd ../

docker-compose up -d