addPet_parseArgs() {
  arg_id=
  arg_category=
  arg_name=
  arg_photoUrls=()
  arg_tags=()
  arg_status=

  while [ "${#@}" -gt 0 ]; do
    case "$1" in
      --id)
        arg_id="$2"
        shift 2
        ;;
      --category)
        arg_category="$2"
        shift 2
        ;;
      --name)
        arg_name="$2"
        shift 2
        ;;
      --photoUrls)
        arg_photoUrls[${#arg_photoUrls[@]}]="$2"
        shift 2
        ;;
      --tags)
        # maybe structures should be handled like:
        #   --tags '--id 42 --name="bar"'
        # This would lend itself to argument assembly via array, then
        # interpolation similar to args[0]="$@"

        # An alternate approach could be:
        #   --tags0_id 42 --tags0_name bar
        # which is both unfortunate to consume by humans and mixes parsing the
        # arg name with parsing structure

        # A third approach could be:
        #   --tags --id 42 --name bar
        # or
        #   --tags --id 42 --name bar --end
        # or
        #   --tags --id 42 --name bar --end-tags
        # This has the advantage of argument parsing as a stack, deferring
        # parsing to the downstream structure parser, then jq -r'ing the
        # back via stdout
        ;;
      --status)
        arg_status="$2"
        shift 2
        ;;
      *)
        if [[ "$1" = --*=* ]]; then
          arg="$1"
          value="${arg#*=}"
          arg="${arg%%=*}"
          shift
          set - "$arg" "$value" "$@"
        else
          return 1
        fi
    esac
  done

  echo arg_id="$arg_id"
  echo arg_category="$arg_category"
  echo arg_name="$arg_name"
  echo arg_photoUrls="( ${arg_photoUrls[@]} )"
  echo arg_tags="( ${arg_tags[@]} )"
  echo arg_status="$arg_status"
}

addPet() {

  eval $(addPet_parseArgs "$@")

  echo '{}' | jq \
    --arg arg_id "$arg_id" \
    --arg arg_category "$arg_category" \
    '. as $obj | (($obj.foo = $arg_id).category = $arg_category) as $obj | $obj'
  # curl 'http://localhost:8000/pet' -X POST
}

addPet --id=42 --category foo --name=bar --photoUrls foo --photoUrls bar --status foo
echo $?

#    "Pet": {
#      "type": "object",
#      "required": [
#        "name",
#        "photoUrls"
#      ],
#      "properties": {
#        "id": {
#          "type": "integer",
#          "format": "int64"
#        },
#        "category": {
#          "$ref": "#/definitions/Category"
#        },
#        "name": {
#          "type": "string",
#          "example": "doggie"
#        },
#        "photoUrls": {
#          "type": "array",
#          "items": {
#            "type": "string"
#          }
#        },
#        "tags": {
#          "type": "array",
#          "items": {
#            "$ref": "#/definitions/Tag"
#          }
#        },
#        "status": {
#          "type": "string",
#          "description": "pet status in the store",
#          "x-scala-type": "PetStatus"
#        }
#      }
#    }
#    "Tag": {
#      "type": "object",
#      "properties": {
#        "id": {
#          "type": "integer",
#          "format": "int64"
#        },
#        "name": {
#          "type": "string"
#        }
#      },
#    },
#
#  "paths": {
#    "/pet": {
#      "post": {
#        "responses": {
#          "201": {
#            "description": "Created"
#          },
#          "400": {
#            "description": "Invalid input"
#          }
#        },
#        "security": [
#          {
#            "petstore_auth": [
#              "write:pets",
#              "read:pets"
#            ]
#          }
#        ]
#      },
#      "put": {
#        "tags": [
#          "pet"
#        ],
#        "x-scala-package": "pet",
#        "summary": "Update an existing pet",
#        "description": "",
#        "operationId": "updatePet",
#        "consumes": [
#          "application/json",
#          "application/xml"
#        ],
#        "produces": [
#          "application/xml",
#          "application/json"
#        ],
#        "parameters": [
#          {
#            "in": "body",
#            "name": "body",
#            "description": "Pet object that needs to be added to the store",
#            "required": true,
#            "schema": {
#              "$ref": "#/definitions/Pet"
#            }
#          }
#        ],
#        "responses": {
#          "400": {
#            "description": "Invalid ID supplied"
#          },
#          "404": {
#            "description": "Pet not found"
#          },
#          "400": {
#            "description": "Validation exception"
#          }
#        },
#        "security": [
#          {
#            "petstore_auth": [
#              "write:pets",
#              "read:pets"
#            ]
#          }
#        ]
#      }
#    },
#    "/pet/findByStatus/{status}": {
#      "get": {
#        "tags": [
#          "pet"
#        ],
#        "x-scala-package": "pet",
#        "summary": "Finds Pets by status",
#        "description": "Multiple status values can be provided with comma separated strings",
#        "operationId": "findPetsByStatusEnum",
#        "produces": [
#          "application/json"
#        ],
#        "parameters": [
#          {
#            "name": "status",
#            "in": "path",
#            "type": "string",
#            "x-scala-type": "PetStatus"
#          }
#        ],
#        "responses": {
#          "200": {
#            "description": "successful operation",
#            "schema": {
#              "type": "array",
#              "items": {
#                "$ref": "#/definitions/Pet"
#              }
#            }
#          },
#          "400": {
#            "description": "Invalid status value"
#          }
#        },
#        "security": [
#          {
#            "petstore_auth": [
#              "write:pets",
#              "read:pets"
#            ]
#          }
#        ]
#      }
#    },
#    "/pet/findByStatus": {
#      "get": {
#        "tags": [
#          "pet"
#        ],
#        "x-scala-package": "pet",
#        "summary": "Finds Pets by status",
#        "description": "Multiple status values can be provided with comma separated strings",
#        "operationId": "findPetsByStatus",
#        "produces": [
#          "application/xml",
#          "application/json"
#        ],
#        "parameters": [
#          {
#            "name": "status",
#            "in": "query",
#            "description": "Status values that need to be considered for filter",
#            "required": true,
#            "type": "array",
#            "items": {
#              "type": "string",
#              "x-scala-type": "PetStatus",
#              "default": "available"
#            },
#            "collectionFormat": "multi"
#          }
#        ],
#        "responses": {
#          "200": {
#            "description": "successful operation",
#            "schema": {
#              "type": "array",
#              "items": {
#                "$ref": "#/definitions/Pet"
#              }
#            }
#          },
#          "400": {
#            "description": "Invalid status value"
#          },
#          "404": {
#            "description": "Pet not found"
#          }
#        },
#        "security": [
#          {
#            "petstore_auth": [
#              "write:pets",
#              "read:pets"
#            ]
#          }
#        ]
#      }
#    },
#    "/pet/findByTags": {
#      "get": {
#        "tags": [
#          "pet"
#        ],
#        "x-scala-package": "pet",
#        "summary": "Finds Pets by tags",
#        "description": "Muliple tags can be provided with comma separated strings. Use tag1, tag2, tag3 for testing.",
#        "operationId": "findPetsByTags",
#        "produces": [
#          "application/xml",
#          "application/json"
#        ],
#        "parameters": [
#          {
#            "name": "tags",
#            "in": "query",
#            "description": "Tags to filter by",
#            "required": true,
#            "type": "array",
#            "items": {
#              "type": "string"
#            },
#            "collectionFormat": "multi"
#          }
#        ],
#        "responses": {
#          "200": {
#            "description": "successful operation",
#            "schema": {
#              "type": "array",
#              "items": {
#                "$ref": "#/definitions/Pet"
#              }
#            }
#          },
#          "400": {
#            "description": "Invalid tag value"
#          }
#        },
#        "security": [
#          {
#            "petstore_auth": [
#              "write:pets",
#              "read:pets"
#            ]
#          }
#        ],
#        "deprecated": true
#      }
#    },
#    "/pet/{petId}": {
#      "get": {
#        "tags": [
#          "pet"
#        ],
#        "x-scala-package": "pet",
#        "summary": "Find pet by ID",
#        "description": "Returns a single pet",
#        "operationId": "getPetById",
#        "produces": [
#          "application/xml",
#          "application/json"
#        ],
#        "parameters": [
#          {
#            "name": "petId",
#            "in": "path",
#            "description": "ID of pet to return",
#            "required": true,
#            "type": "integer",
#            "format": "int64"
#          }
#        ],
#        "responses": {
#          "200": {
#            "description": "successful operation",
#            "schema": {
#              "$ref": "#/definitions/Pet"
#            }
#          },
#          "400": {
#            "description": "Invalid ID supplied"
#          },
#          "404": {
#            "description": "Pet not found"
#          }
#        },
#        "security": [
#          {
#            "api_key": []
#          }
#        ]
#      },
#      "post": {
#        "tags": [
#          "pet"
#        ],
#        "x-scala-package": "pet",
#        "summary": "Updates a pet in the store with form data",
#        "description": "",
#        "operationId": "updatePetWithForm",
#        "consumes": [
#          "application/x-www-form-urlencoded"
#        ],
#        "produces": [
#          "application/xml",
#          "application/json"
#        ],
#        "parameters": [
#          {
#            "name": "petId",
#            "in": "path",
#            "description": "ID of pet that needs to be updated",
#            "required": true,
#            "type": "integer",
#            "format": "int64"
#          },
#          {
#            "name": "name",
#            "in": "formData",
#            "description": "Updated name of the pet",
#            "required": false,
#            "type": "string"
#          },
#          {
#            "name": "status",
#            "in": "formData",
#            "description": "Updated status of the pet",
#            "required": false,
#            "type": "string"
#          }
#        ],
#        "responses": {
#          "400": {
#            "description": "Invalid input"
#          }
#        },
#        "security": [
#          {
#            "petstore_auth": [
#              "write:pets",
#              "read:pets"
#            ]
#          }
#        ]
#      },
#      "delete": {
#        "tags": [
#          "pet"
#        ],
#        "x-scala-package": "pet",
#        "summary": "Deletes a pet",
#        "description": "",
#        "operationId": "deletePet",
#        "produces": [
#          "application/xml",
#          "application/json"
#        ],
#        "parameters": [
#          {
#            "name": "api_key",
#            "in": "header",
#            "required": false,
#            "type": "string"
#          },
#          {
#            "name": "includeChildren",
#            "in": "query",
#            "description": "Delete children as well",
#            "type": "boolean"
#          },
#          {
#            "name": "status",
#            "in": "query",
#            "description": "Only delete pets with the specified status",
#            "type": "string",
#            "x-scala-type": "PetStatus"
#          },
#          {
#            "name": "petId",
#            "in": "path",
#            "description": "Pet id to delete",
#            "required": true,
#            "type": "integer",
#            "format": "int64"
#          }
#        ],
#        "responses": {
#          "200": {
#            "description": "Pet deleted"
#          },
#          "400": {
#            "description": "Invalid ID supplied"
#          },
#          "404": {
#            "description": "Pet not found"
#          }
#        },
#        "security": [
#          {
#            "petstore_auth": [
#              "write:pets",
#              "read:pets"
#            ]
#          }
#        ]
#      }
#    },
#    "/pet/{petId}/uploadImage": {
#      "post": {
#        "tags": [
#          "pet"
#        ],
#        "x-scala-package": "pet",
#        "summary": "uploads an image",
#        "description": "",
#        "operationId": "uploadFile",
#        "consumes": [
#          "multipart/form-data"
#        ],
#        "produces": [
#          "application/json"
#        ],
#        "parameters": [
#          {
#            "name": "petId",
#            "in": "path",
#            "description": "ID of pet to update",
#            "required": true,
#            "type": "integer",
#            "format": "int64",
#            "x-scala-type": "PositiveLong"
#          },
#          {
#            "name": "additionalMetadata",
#            "in": "formData",
#            "description": "Additional data to pass to server",
#            "required": false,
#            "type": "string"
#          },
#          {
#            "name": "file",
#            "in": "formData",
#            "description": "file to upload",
#            "required": false,
#            "type": "file"
#          },
#          {
#            "name": "file2",
#            "in": "formData",
#            "description": "file to upload",
#            "required": true,
#            "type": "file"
#          },
#          {
#            "name": "file3",
#            "in": "formData",
#            "description": "file to upload",
#            "required": true,
#            "type": "file",
#            "x-scala-file-hash": "SHA-256"
#          },
#          {
#            "name": "long-value",
#            "in": "formData",
#            "required": true,
#            "type": "integer",
#            "format": "int64"
#          },
#          {
#            "name": "custom-value",
#            "in": "formData",
#            "required": true,
#            "type": "integer",
#            "format": "int64",
#            "x-scala-type": "PositiveLong"
#          },
#          {
#            "name": "custom-optional-value",
#            "in": "formData",
#            "type": "number",
#            "format": "int64",
#            "x-scala-type": "PositiveLong"
#          }
#        ],
#        "responses": {
#          "200": {
#            "description": "successful operation",
#            "schema": {
#              "$ref": "#/definitions/ApiResponse"
#            }
#          }
#        },
#        "security": [
#          {
#            "petstore_auth": [
#              "write:pets",
#              "read:pets"
#            ]
#          }
#        ]
#      }
#    },
#    "/store/inventory": {
#      "get": {
#        "tags": [
#          "store"
#        ],
#        "x-scala-package": "store",
#        "summary": "Returns pet inventories by status",
#        "description": "Returns a map of status codes to quantities",
#        "operationId": "getInventory",
#        "produces": [
#          "application/json"
#        ],
#        "parameters": [],
#        "responses": {
#          "200": {
#            "description": "successful operation",
#            "schema": {
#              "type": "object",
#              "additionalProperties": {
#                "type": "integer",
#                "format": "int32"
#              }
#            }
#          }
#        },
#        "security": [
#          {
#            "api_key": []
#          }
#        ]
#      }
#    },
#    "/store/order": {
#      "post": {
#        "tags": [
#          "store"
#        ],
#        "x-scala-package": "store",
#        "summary": "Place an order for a pet",
#        "description": "",
#        "operationId": "placeOrder",
#        "produces": [
#          "application/xml",
#          "application/json"
#        ],
#        "parameters": [
#          {
#            "in": "body",
#            "name": "body",
#            "description": "order placed for purchasing the pet",
#            "required": true,
#            "schema": {
#              "$ref": "#/definitions/Order"
#            }
#          }
#        ],
#        "responses": {
#          "200": {
#            "description": "successful operation",
#            "schema": {
#              "$ref": "#/definitions/Order"
#            }
#          },
#          "400": {
#            "description": "Invalid Order"
#          }
#        }
#      }
#    },
#    "/store/order/{orderId}": {
#      "get": {
#        "tags": [
#          "store"
#        ],
#        "x-scala-package": "store",
#        "summary": "Find purchase order by ID",
#        "description": "For valid response try integer IDs with value >= 1 and <= 10. Other values will generated exceptions",
#        "operationId": "getOrderById",
#        "produces": [
#          "application/xml",
#          "application/json"
#        ],
#        "parameters": [
#          {
#            "name": "orderId",
#            "in": "path",
#            "description": "ID of pet that needs to be fetched",
#            "required": true,
#            "type": "integer",
#            "maximum": 10,
#            "minimum": 1,
#            "format": "int64"
#          }
#        ],
#        "responses": {
#          "200": {
#            "description": "successful operation",
#            "schema": {
#              "$ref": "#/definitions/Order"
#            }
#          },
#          "400": {
#            "description": "Invalid ID supplied"
#          },
#          "404": {
#            "description": "Order not found"
#          }
#        }
#      },
#      "delete": {
#        "tags": [
#          "store"
#        ],
#        "x-scala-package": "store",
#        "summary": "Delete purchase order by ID",
#        "description": "For valid response try integer IDs with positive integer value. Negative or non-integer values will generate API errors",
#        "operationId": "deleteOrder",
#        "produces": [
#          "application/xml",
#          "application/json"
#        ],
#        "parameters": [
#          {
#            "name": "orderId",
#            "in": "path",
#            "description": "ID of the order that needs to be deleted",
#            "required": true,
#            "type": "integer",
#            "minimum": 1,
#            "format": "int64"
#          }
#        ],
#        "responses": {
#          "400": {
#            "description": "Invalid ID supplied"
#          },
#          "404": {
#            "description": "Order not found"
#          }
#        }
#      }
#    },
#    "/user": {
#      "post": {
#        "tags": [
#          "user"
#        ],
#        "x-scala-package": "user",
#        "summary": "Create user",
#        "description": "This can only be done by the logged in user.",
#        "operationId": "createUser",
#        "produces": [
#          "application/xml",
#          "application/json"
#        ],
#        "parameters": [
#          {
#            "in": "body",
#            "name": "body",
#            "description": "Created user object",
#            "required": true,
#            "schema": {
#              "$ref": "#/definitions/User"
#            }
#          }
#        ],
#        "responses": {
#          "200": {
#            "description": "successful operation"
#          }
#        }
#      }
#    },
#    "/user/createWithArray": {
#      "post": {
#        "tags": [
#          "user"
#        ],
#        "x-scala-package": "user",
#        "summary": "Creates list of users with given input array",
#        "description": "",
#        "operationId": "createUsersWithArrayInput",
#        "produces": [
#          "application/xml",
#          "application/json"
#        ],
#        "parameters": [
#          {
#            "in": "body",
#            "name": "body",
#            "description": "List of user object",
#            "required": true,
#            "schema": {
#              "type": "array",
#              "items": {
#                "$ref": "#/definitions/User"
#              }
#            }
#          }
#        ],
#        "responses": {
#          "200": {
#            "description": "successful operation"
#          }
#        }
#      }
#    },
#    "/user/createWithList": {
#      "post": {
#        "tags": [
#          "user"
#        ],
#        "x-scala-package": "user",
#        "summary": "Creates list of users with given input array",
#        "description": "",
#        "operationId": "createUsersWithListInput",
#        "produces": [
#          "application/xml",
#          "application/json"
#        ],
#        "parameters": [
#          {
#            "in": "body",
#            "name": "body",
#            "description": "List of user object",
#            "required": true,
#            "schema": {
#              "type": "array",
#              "items": {
#                "$ref": "#/definitions/User"
#              }
#            }
#          }
#        ],
#        "responses": {
#          "200": {
#            "description": "successful operation"
#          }
#        }
#      }
#    },
#    "/user/login": {
#      "get": {
#        "tags": [
#          "user"
#        ],
#        "x-scala-package": "user",
#        "summary": "Logs user into the system",
#        "description": "",
#        "operationId": "loginUser",
#        "produces": [
#          "application/xml",
#          "application/json"
#        ],
#        "parameters": [
#          {
#            "name": "username",
#            "in": "query",
#            "description": "The user name for login",
#            "required": true,
#            "type": "string"
#          },
#          {
#            "name": "password",
#            "in": "query",
#            "description": "The password for login in clear text",
#            "required": true,
#            "type": "string"
#          }
#        ],
#        "responses": {
#          "200": {
#            "description": "successful operation",
#            "schema": {
#              "type": "string"
#            },
#            "headers": {
#              "X-Rate-Limit": {
#                "type": "integer",
#                "format": "int32",
#                "description": "calls per hour allowed by the user"
#              },
#              "X-Expires-After": {
#                "type": "string",
#                "format": "date-time",
#                "description": "date in UTC when token expires"
#              }
#            }
#          },
#          "400": {
#            "description": "Invalid username/password supplied"
#          }
#        }
#      }
#    },
#    "/user/logout": {
#      "get": {
#        "tags": [
#          "user"
#        ],
#        "x-scala-package": "user",
#        "summary": "Logs out current logged in user session",
#        "description": "",
#        "operationId": "logoutUser",
#        "produces": [
#          "application/xml",
#          "application/json"
#        ],
#        "parameters": [],
#        "responses": {
#          "200": {
#            "description": "successful operation"
#          }
#        }
#      }
#    },
#    "/user/{username}": {
#      "get": {
#        "tags": [
#          "user"
#        ],
#        "x-scala-package": "user",
#        "summary": "Get user by user name",
#        "description": "",
#        "operationId": "getUserByName",
#        "produces": [
#          "application/xml",
#          "application/json"
#        ],
#        "parameters": [
#          {
#            "name": "username",
#            "in": "path",
#            "description": "The name that needs to be fetched. Use user1 for testing. ",
#            "required": true,
#            "type": "string"
#          }
#        ],
#        "responses": {
#          "200": {
#            "description": "successful operation",
#            "schema": {
#              "$ref": "#/definitions/User"
#            }
#          },
#          "400": {
#            "description": "Invalid username supplied"
#          },
#          "404": {
#            "description": "User not found"
#          }
#        }
#      },
#      "put": {
#        "tags": [
#          "user"
#        ],
#        "x-scala-package": "user",
#        "summary": "Updated user",
#        "description": "This can only be done by the logged in user.",
#        "operationId": "updateUser",
#        "produces": [
#          "application/xml",
#          "application/json"
#        ],
#        "parameters": [
#          {
#            "name": "username",
#            "in": "path",
#            "description": "name that need to be updated",
#            "required": true,
#            "type": "string"
#          },
#          {
#            "in": "body",
#            "name": "body",
#            "description": "Updated user object",
#            "required": true,
#            "schema": {
#              "$ref": "#/definitions/User"
#            }
#          }
#        ],
#        "responses": {
#          "400": {
#            "description": "Invalid user supplied"
#          },
#          "404": {
#            "description": "User not found"
#          }
#        }
#      },
#      "delete": {
#        "tags": [
#          "user"
#        ],
#        "x-scala-package": "user",
#        "summary": "Delete user",
#        "description": "This can only be done by the logged in user.",
#        "operationId": "deleteUser",
#        "produces": [
#          "application/xml",
#          "application/json"
#        ],
#        "parameters": [
#          {
#            "name": "username",
#            "in": "path",
#            "description": "The name that needs to be deleted",
#            "required": true,
#            "type": "string"
#          }
#        ],
#        "responses": {
#          "400": {
#            "description": "Invalid username supplied"
#          },
#          "404": {
#            "description": "User not found"
#          }
#        }
#      }
#    }
#  },
