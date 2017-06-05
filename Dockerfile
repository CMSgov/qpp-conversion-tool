FROM openjdk:8

ENV SCRIPT_URL=https://gist.githubusercontent.com/clydet/d50c6d8ef8c6f4aea8ca8fe3c889f03e/raw/5e31fd7d343050271a9498f8587eda55b97be6c1/getConverterArtifact.sh

RUN mkdir -p /usr/src/run/
WORKDIR /usr/src/run/
ADD $SCRIPT_URL ./getConverterArtifact.sh
RUN chmod +x ./getConverterArtifact.sh
RUN ./getConverterArtifact.sh
RUN chmod 777 ./rest-api.jar

EXPOSE 8080
CMD java -jar ./rest-api.jar