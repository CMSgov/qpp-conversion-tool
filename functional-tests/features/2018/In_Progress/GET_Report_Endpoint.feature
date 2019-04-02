@tcID_261994 @qppct_devprev
@testRail @rubin @qppct_imp_api
Feature: Validate that the ct repository's sample files successfully convert

  Scenario: QPPCT-975 and QPPCT-976 test
    When User authenticates the QPPCT API with role=CPCPLUSJWT
    When User makes GET request to "/cpc/processed-files"
    And  User receives 200 response code
    And  User generates an examples table using the fileId of the response
    And  I keep the JSON response at "0/fileId" as "File_ID"
    And  User makes GET request to "/cpc/report/%{File_ID}"
    Then User receives 200 response code
    And  the JSON response at "warnings" should be an array

  Scenario Outline: QPPCT-1007 get /report for all files before 2018 should generate 422 reponse code.
    When User authenticates the QPPCT API with role=CPCPLUSJWT
    And  User makes GET request to "/cpc/report/<fileId>"
    Then User receives 422 response code

    Examples:
      | fileId                               |
      | 419fffac-65e5-4e48-88af-043efc6a0ff0 |
      | 3f6cb738-5842-4850-8b9e-e70140a44f92 |
      | ee632080-a4ce-460f-a6dc-f214f43b8b41 |
      | 7304268f-8144-4be2-b106-8b47bca10353 |
      | 70620932-2501-4abf-b5ec-d4a88bfd1608 |
      | 72c1a4eb-93db-4d44-926c-cd1e40b9e40e |
      | 206e7674-f430-4f8f-9bc7-fbdf5a4cb599 |
      | 63c64c96-b185-4e61-9d57-47475cfb0062 |
      | c203022f-cc2e-4cce-8b24-0d7e80c6fa8d |
      | e9e5d77d-3196-4ac4-9180-72ad08863f04 |
      | 375176d8-6352-430f-b50b-5b2bc0ec9931 |
      | d29e01e7-764c-43e1-8645-bc37d95dab15 |
      | f5dc4c95-d8da-432c-af10-a5791920dd8d |
      | 1e4206b9-27bc-4ce1-8df9-3c918f3f7a08 |
      | f3e14187-6506-4b94-bd76-aca599b51acf |
      | 90763842-07c9-4eae-9591-debfb29dd1f1 |
      | eda645b9-8dc1-4cbf-aaf3-316d03ba8cc9 |
      | 45e94a00-9f2f-4af7-9b0f-d4ba6f8be5c8 |
      | a5687b9e-21b2-4e5a-b3f9-1f90e557a6c3 |
      | 566f836e-e894-4590-93d1-b96f94d5b8e5 |
      | 3740e68a-2e20-42cc-89fa-fe11f64d08d7 |
      | c7e906e6-90ed-4556-ae6c-e45a38feb82a |
      | 08bed8fc-3af1-4033-b30b-c6a2a9db603d |
      | fc0f29b5-d41a-46d8-bc05-6c078c106124 |
      | 506d1d3d-3352-4351-8ecd-5189b11f8ce2 |
      | b9b293f9-cde3-4c00-8f60-08f885053a59 |
      | fa75113d-cd03-4fba-a56a-32dfbf836c53 |
      | 0e2c806b-7f48-4c33-bbf4-694f0533ede1 |
      | b4d3be6b-78ea-4157-8e9b-522c3b58b9f3 |
      | bc2c5b67-0300-4c10-b6de-ebda36c086f4 |
      | 0d0193a8-c5a5-4ea5-b2e6-670fcc98aef2 |
      | 7e355c3a-c62d-4abe-9f87-c40fda61f49f |
      | 3486bf63-6226-4b9d-8cb2-12fa382e4812 |
      | ec903f6b-0899-4b49-96a1-71e099e105bc |
      | 1d70ed49-168b-47f8-98c6-a25020291948 |
      | 0dc2a2c1-2b2d-4f7d-aee4-79f943e404d7 |
      | 4c24f00c-270e-4452-9d87-da9a3d01d89b |
      | 059a4fdc-cb88-4803-b400-96aae9fdf01b |
      | c6526df6-a7fe-46b3-b722-5f82d6f70b55 |
      | 58f655cd-6ccc-4079-b984-1de96fbf7b1a |
      | 007caee8-6bc4-4fb0-9928-66f584098cdd |
      | da8cdd27-a555-4eb7-b687-590ac6b6e152 |
      | 3d1d9eae-1baa-4d81-8e1e-d73a854d1a59 |
      | bd382bb3-a5e9-4a2a-99e8-4c1e5f788ac0 |
      | 4711bdd8-4e55-4534-a6ae-bda5dfb3654c |
      | 243ffd11-40ba-463c-bb92-09e5732560b1 |
      | 74d948ea-2294-4b3e-8f4e-7461dded06ee |
      | 682b2eb8-b1db-4ae5-a668-d4eb39fe5c9c |
      | 95d2068c-21cc-4830-97f2-26d5afdd3ed4 |
      | 254996f9-dead-4384-a167-a16c49508f83 |
      | 2f4f7e25-ee4a-44f7-8b46-64c002810e72 |
      | dd476c93-d834-4512-a002-7fb4d6bbdf04 |
      | 903f671b-a9bf-4f02-ac0e-4a57ba19419d |
      | 4ad9679f-d45c-439f-b061-106a2747b720 |
      | 34eea17b-a5d4-4468-8d02-ef2c6a75e516 |
      | 1da5c7ae-d0b0-4751-811a-0ce8c9d6c2a7 |
      | 1ff1d9b5-bcbb-47d2-8450-b32264cc7737 |
      | c9fc47e1-0b24-401c-812a-5f13b779cf01 |
      | c2784586-36ee-4966-8124-26075901617b |
      | a194d6e8-66da-473a-b231-b38fc38257b5 |
      | 362fbdf8-46f9-44d5-bfe1-b45dd1481a57 |
      | a7134bdd-e4a8-4517-afc7-7b1260593b56 |
      | 9a3b7c98-e48c-4994-bdf0-8e2e0819c4b8 |
      | 3256c721-1de3-4566-bcbf-19aa7186d516 |
      | e2206dd4-3240-47f8-8157-e302f263b8af |
      | ae924c55-9b9e-4263-9aa9-665b95abbd83 |
      | 39accdc2-cde9-4307-802d-12921690ef36 |
      | d4d9f921-7e92-44f5-8ae5-09c98cf588b5 |
      | 173d1c9c-323e-490c-a2d7-f5bf9b4a1384 |
      | 8eab36b5-cca1-4795-b749-95e8da62b10c |
      | 61d76984-59a6-4efe-8fd4-25a74fa16b8f |
      | 8f2676af-a066-4cb0-88db-55bbe1abd407 |
      | 2841bbd5-67d5-4dd3-baf4-6d177972ecf2 |
      | 8ddf94fe-6adb-4c86-81c7-ca84fa04e9f3 |
      | 88ec785e-d39e-45b9-baab-b453f202417e |
      | 7c0e4aa8-d801-4fad-88c3-4f0af556f2ca |
      | cb73807d-7b92-4d59-a649-a8145f6356bd |
      | 9d543c8b-9b7c-459b-b756-a1583d0498a8 |
      | b01326cb-1e73-416a-b70e-0a7f9d049d62 |
      | 30079009-efce-4a2a-82d5-340870aaf810 |
      | b8b2aeb4-f503-4246-b7e4-e78394a960df |
      | 386125b3-3264-43ca-a8f2-c88deda6a004 |
      | ed7e3ef5-634c-43ff-be13-6b4887b0a50d |
      | 0e8de36b-e14d-4c75-959f-b9d5982213b7 |
      | c065cc07-84b4-42ce-a4e7-7cd92b9413a3 |
      | 238ebb0c-ae45-4b3a-8b57-633d65c58a92 |
      | e3a43b95-704c-4869-add4-17ce98cd4714 |
      | 4fccd877-d9c8-4ee6-8d21-d9cb6637098f |
      | fa8b0079-0ff4-45a5-bfa3-46611af242e1 |
      | 7dcb9858-9005-472e-84a0-61a85c7ce387 |
      | 385a8bca-2742-461b-9916-468175771f59 |
      | 2ee9b28f-cc37-4431-9f29-7bc4c545a776 |
      | ce7aedf3-4b37-4c8d-b013-5fb67625b1c2 |
      | e3263299-70ab-446d-9fb5-137594295776 |
      | 94063961-a589-4a01-b787-a168747e7cab |
      | 036dd7ec-7758-4171-9912-53c5d5da128c |
