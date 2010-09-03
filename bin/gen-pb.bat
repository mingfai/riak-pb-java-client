@Echo off
SET PROTOC_HOME=C:\java\tools\protoc-2.3.0
SET SRC_DIR=src/main/protobuf

%PROTOC_HOME%/protoc -I=%SRC_DIR% --java_out=src/main/resources %SRC_DIR%/*.proto 