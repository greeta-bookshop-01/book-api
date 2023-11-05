docker-compose down

cd ../book-ui
./gradlew buildAngular
pack build book-ui --buildpack gcr.io/paketo-buildpacks/nginx --builder paketobuildpacks/builder:base -p dist

cd ../book-api

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