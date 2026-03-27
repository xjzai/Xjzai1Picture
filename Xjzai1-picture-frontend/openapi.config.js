// 作者: Liu Jiaxu (B01051251)

import { generateService } from '@umijs/openapi'

generateService({
  requestLibPath: "import request from '@/plugins/myAxios'",
  schemaPath: 'http://localhost:8123/api/v2/api-docs',
  serversPath: './src',
})
