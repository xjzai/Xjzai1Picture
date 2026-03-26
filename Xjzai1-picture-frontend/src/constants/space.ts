export const SPACE_LEVEL_ENUM = {
  COMMON: 0,
  PROFESSIONAL: 1,
  FLAGSHIP: 2,
} as const;

// 空间级别文本映射
export const SPACE_LEVEL_MAP: Record<number, string> = {
  0: 'Basic',
  1: 'Professional',
  2: 'Enterprise',
};

// 空间级别选项映射
export const SPACE_LEVEL_OPTIONS = Object.keys(SPACE_LEVEL_MAP).map((key) => {
  const value = Number(key); // Convert string key to number
  return {
    label: SPACE_LEVEL_MAP[value],
    value,
  };
});

// 空间类型枚举
export const SPACE_TYPE_ENUM = {
  PRIVATE: 0,
  TEAM: 1,
}

// 空间类型文本映射
export const SPACE_TYPE_MAP: Record<number, string> = {
  0: 'Private Space',
  1: 'Team Space',
}

// 空间类型选项映射
export const SPACE_TYPE_OPTIONS = Object.keys(SPACE_TYPE_MAP).map((key) => {
  const value = Number(key) // 将字符串 key 转换为数字
  return {
    label: SPACE_TYPE_MAP[value],
    value,
  }
})

// 空间角色枚举
export const SPACE_ROLE_ENUM = {
  VIEWER: "viewer",
  EDITOR: "editor",
  ADMIN: "admin",
} as const;

// 空间角色文本映射
export const SPACE_ROLE_MAP: Record<string, string> = {
  viewer: "Viewer",
  editor: "Editor",
  admin: "Admin",
};

// 空间角色选项映射
export const SPACE_ROLE_OPTIONS = Object.keys(SPACE_ROLE_MAP).map((key) => {
  return {
    label: SPACE_ROLE_MAP[key],
    value: key,
  };
});

/**
 * 空间权限常量
 */
export const SPACE_PERMISSION_ENUM = {
  SPACE_USER_MANAGE: "spaceUser:manage",
  PICTURE_VIEW: "picture:view",
  PICTURE_UPLOAD: "picture:upload",
  PICTURE_EDIT: "picture:edit",
  PICTURE_DELETE: "picture:delete",
} as const;

