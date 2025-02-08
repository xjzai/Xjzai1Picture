import { generateService } from '@umijs/openapi'

generateService({
  requestLibPath: "import myAxios from '@/plugins/myAxios'",
  schemaPath: 'http://localhost:8123/api/v2/api-docs',
  serversPath: './src',
})
