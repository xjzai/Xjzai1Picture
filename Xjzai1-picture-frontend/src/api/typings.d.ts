declare namespace API {
  type BaseResponseBoolean_ = {
    code?: number
    data?: boolean
    description?: string
    message?: string
  }

  type BaseResponseInt_ = {
    code?: number
    data?: number
    description?: string
    message?: string
  }

  type BaseResponseLong_ = {
    code?: number
    data?: number
    description?: string
    message?: string
  }

  type BaseResponsePagePicture_ = {
    code?: number
    data?: PagePicture_
    description?: string
    message?: string
  }

  type BaseResponsePagePictureVo_ = {
    code?: number
    data?: PagePictureVo_
    description?: string
    message?: string
  }

  type BaseResponsePageUserVo_ = {
    code?: number
    data?: PageUserVo_
    description?: string
    message?: string
  }

  type BaseResponsePicture_ = {
    code?: number
    data?: Picture
    description?: string
    message?: string
  }

  type BaseResponsePictureTagCategory_ = {
    code?: number
    data?: PictureTagCategory
    description?: string
    message?: string
  }

  type BaseResponsePictureVo_ = {
    code?: number
    data?: PictureVo
    description?: string
    message?: string
  }

  type BaseResponseString_ = {
    code?: number
    data?: string
    description?: string
    message?: string
  }

  type BaseResponseUser_ = {
    code?: number
    data?: User
    description?: string
    message?: string
  }

  type BaseResponseUserLoginVo_ = {
    code?: number
    data?: UserLoginVo
    description?: string
    message?: string
  }

  type BaseResponseUserVo_ = {
    code?: number
    data?: UserVo
    description?: string
    message?: string
  }

  type DeleteRequest = {
    id?: number
  }

  type getPictureByIdUsingGETParams = {
    /** id */
    id?: number
  }

  type getPictureVoByIdUsingGETParams = {
    /** id */
    id?: number
  }

  type getUserByIdUsingGETParams = {
    /** id */
    id?: number
  }

  type getUserVoByIdUsingGETParams = {
    /** id */
    id?: number
  }

  type OrderItem = {
    asc?: boolean
    column?: string
  }

  type PagePicture_ = {
    countId?: string
    current?: number
    maxLimit?: number
    optimizeCountSql?: boolean
    orders?: OrderItem[]
    pages?: number
    records?: Picture[]
    searchCount?: boolean
    size?: number
    total?: number
  }

  type PagePictureVo_ = {
    countId?: string
    current?: number
    maxLimit?: number
    optimizeCountSql?: boolean
    orders?: OrderItem[]
    pages?: number
    records?: PictureVo[]
    searchCount?: boolean
    size?: number
    total?: number
  }

  type PageUserVo_ = {
    countId?: string
    current?: number
    maxLimit?: number
    optimizeCountSql?: boolean
    orders?: OrderItem[]
    pages?: number
    records?: UserVo[]
    searchCount?: boolean
    size?: number
    total?: number
  }

  type Picture = {
    category?: string
    createTime?: string
    editTime?: string
    id?: number
    introduction?: string
    isDelete?: number
    name?: string
    originalUrl?: string
    pictureFormat?: string
    pictureHeight?: number
    pictureScale?: number
    pictureSize?: number
    pictureWidth?: number
    reviewMessage?: string
    reviewStatus?: number
    reviewTime?: string
    reviewerId?: number
    tags?: string
    thumbnailUrl?: string
    updateTime?: string
    url?: string
    userId?: number
  }

  type PictureEditRequest = {
    category?: string
    id?: number
    introduction?: string
    name?: string
    tags?: string[]
  }

  type PictureQueryRequest = {
    category?: string
    current?: number
    id?: number
    introduction?: string
    name?: string
    pageSize?: number
    pictureFormat?: string
    pictureHeight?: number
    pictureScale?: number
    pictureSize?: number
    pictureWidth?: number
    reviewMessage?: string
    reviewStatus?: number
    reviewerId?: number
    searchText?: string
    sortField?: string
    sortOrder?: string
    tags?: string[]
    userId?: number
  }

  type PictureReviewRequest = {
    id?: number
    reviewMessage?: string
    reviewStatus?: number
  }

  type PictureTagCategory = {
    categoryList?: string[]
    tagList?: string[]
  }

  type PictureUpdateRequest = {
    category?: string
    id?: number
    introduction?: string
    name?: string
    tags?: string[]
  }

  type PictureUploadByBatchRequest = {
    count?: number
    namePrefix?: string
    searchText?: string
  }

  type PictureUploadRequest = {
    fileUrl?: string
    id?: number
    pictureName?: string
  }

  type PictureVo = {
    category?: string
    createTime?: string
    editTime?: string
    id?: number
    introduction?: string
    name?: string
    originalUrl?: string
    pictureFormat?: string
    pictureHeight?: number
    pictureScale?: number
    pictureSize?: number
    pictureWidth?: number
    tags?: string[]
    thumbnailUrl?: string
    updateTime?: string
    url?: string
    user?: UserVo
    userId?: number
  }

  type testDownloadFileUsingGETParams = {
    /** filepath */
    filepath?: string
  }

  type uploadPictureUsingPOSTParams = {
    fileUrl?: string
    id?: number
    pictureName?: string
  }

  type User = {
    createTime?: string
    editTime?: string
    id?: number
    isDelete?: number
    updateTime?: string
    userAccount?: string
    userAvatar?: string
    userName?: string
    userPassword?: string
    userProfile?: string
    userRole?: string
  }

  type UserAddRequest = {
    userAccount?: string
    userAvatar?: string
    userName?: string
    userProfile?: string
    userRole?: string
  }

  type UserLoginRequest = {
    userAccount?: string
    userPassword?: string
  }

  type UserLoginVo = {
    createTime?: string
    id?: number
    updateTime?: string
    userAccount?: string
    userAvatar?: string
    userName?: string
    userProfile?: string
    userRole?: string
  }

  type UserQueryRequest = {
    current?: number
    id?: number
    pageSize?: number
    sortField?: string
    sortOrder?: string
    userAccount?: string
    userName?: string
    userProfile?: string
    userRole?: string
  }

  type UserRegisterRequest = {
    checkPassword?: string
    userAccount?: string
    userPassword?: string
  }

  type UserUpdateRequest = {
    id?: number
    userAvatar?: string
    userName?: string
    userProfile?: string
    userRole?: string
  }

  type UserVo = {
    createTime?: string
    id?: number
    userAccount?: string
    userAvatar?: string
    userName?: string
    userProfile?: string
    userRole?: string
  }
}
