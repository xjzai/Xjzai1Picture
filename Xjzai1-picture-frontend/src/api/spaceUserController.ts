// @ts-ignore
/* eslint-disable */
import request from '@/plugins/myAxios'

/** addSpaceUser POST /api/spaceUser/add */
export async function addSpaceUserUsingPost(
  body: API.SpaceUserAddRequest,
  options?: { [key: string]: any },
) {
  return request<API.BaseResponseLong_>('/api/spaceUser/add', {
    method: 'POST',
    headers: {
      'Accept-Charset': 'utf-8',
      'Content-Type': 'application/json;charset=UTF-8',
    },
    data: body,
    ...(options || {}),
  })
}

/** deleteSpaceUser POST /api/spaceUser/delete */
export async function deleteSpaceUserUsingPost(
  body: API.DeleteRequest,
  options?: { [key: string]: any },
) {
  return request<API.BaseResponseBoolean_>('/api/spaceUser/delete', {
    method: 'POST',
    headers: {
      'Accept-Charset': 'utf-8',
      'Content-Type': 'application/json;charset=UTF-8',
    },
    data: body,
    ...(options || {}),
  })
}

/** editSpaceUser POST /api/spaceUser/edit */
export async function editSpaceUserUsingPost(
  body: API.SpaceUserEditRequest,
  options?: { [key: string]: any },
) {
  return request<API.BaseResponseBoolean_>('/api/spaceUser/edit', {
    method: 'POST',
    headers: {
      'Accept-Charset': 'utf-8',
      'Content-Type': 'application/json;charset=UTF-8',
    },
    data: body,
    ...(options || {}),
  })
}

/** getSpaceUser POST /api/spaceUser/get */
export async function getSpaceUserUsingPost(
  body: API.SpaceUserQueryRequest,
  options?: { [key: string]: any },
) {
  return request<API.BaseResponseSpaceUser_>('/api/spaceUser/get', {
    method: 'POST',
    headers: {
      'Accept-Charset': 'utf-8',
      'Content-Type': 'application/json;charset=UTF-8',
    },
    data: body,
    ...(options || {}),
  })
}

/** listSpaceUser POST /api/spaceUser/list */
export async function listSpaceUserUsingPost(
  body: API.SpaceUserQueryRequest,
  options?: { [key: string]: any },
) {
  return request<API.BaseResponseListSpaceUserVo_>('/api/spaceUser/list', {
    method: 'POST',
    headers: {
      'Accept-Charset': 'utf-8',
      'Content-Type': 'application/json;charset=UTF-8',
    },
    data: body,
    ...(options || {}),
  })
}

/** listMyTeamSpace POST /api/spaceUser/list/my */
export async function listMyTeamSpaceUsingPost(options?: { [key: string]: any }) {
  return request<API.BaseResponseListSpaceUserVo_>('/api/spaceUser/list/my', {
    method: 'POST',
    ...(options || {}),
  })
}
