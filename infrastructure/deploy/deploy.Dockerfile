FROM python:3.9-slim
RUN apt-get update && apt-get install -yqq git
RUN pip install --no-cache-dir ecs-deploy awscli
