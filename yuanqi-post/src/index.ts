import { Context, Schema } from 'koishi';

// 定义配置接口
interface Config {
  assistantId: string;
  apiToken: string;
  prefix: string;
}

// 定义配置模式（schema）
export const Config: Schema<Config> = Schema.object({
  assistantId: Schema.string().required().description('输入你的智能体id'),
  apiToken: Schema.string().required().description('输入腾讯元器Token'),
  prefix: Schema.string().default('a').description('设置触发词_也就是前缀[触发词] (消息)'),
});

// 递归搜索图片链接的函数
function findImageUrls(obj, accumulator = []) {
  if (typeof obj === 'string') {
    const imageUrlRegex = /https?:\/\/[^\s]+(?:\.(?:png|jpg|jpeg|gif|bmp))/i;
    const match = obj.match(imageUrlRegex);
    if (match) accumulator.push(match[0]);
  } else if (obj !== null && typeof obj === 'object') {
    for (const key in obj) {
      if (Array.isArray(obj[key])) {
        obj[key].forEach(item => findImageUrls(item, accumulator));
      } else {
        findImageUrls(obj[key], accumulator);
      }
    }
  }
  return accumulator;
}

// 应用插件
export function apply(ctx: Context, config: Config) {
  ctx.command(config.prefix, '发送消息给API')
    .action(async ({ args, session }) => {
      const url = 'https://open.hunyuan.tencent.com/openapi/v1/agent/chat/completions';
      const headers = {
        'X-Source': 'openapi',
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${config.apiToken}`, // 使用配置中的Token
      };
      const data = {
        "assistant_id": config.assistantId, // 使用配置中的assistant_id
        "user_id": "username",
        "stream": false,
        "messages": [
          {
            "role": "user",
            "content": [
              {
                "type": "text",
                "text": args.join(' '), // 将所有参数合并为一个字符串
              }
            ]
          }
        ]
      };

      try {
        const response = await fetch(url, {
          method: 'POST',
          headers,
          body: JSON.stringify(data)
        });
        const result = await response.json();
        const imageUrls = findImageUrls(result); // 搜索整个JSON对象

        let messageContent = result.choices[0].message.content || '';
        if (typeof messageContent === 'string') {
          // 过滤掉已经存在于消息内容中的图片链接
          const existingImageUrls = new Set(messageContent.match(/https?:\/\/[^\s]+(?:\.(?:png|jpg|jpeg|gif|bmp))/g) || []);
          const newImageUrls = imageUrls.filter(url => !existingImageUrls.has(url));

          // 如果有新的图片链接，将它们以Markdown格式附加到消息内容的末尾
          if (newImageUrls.length > 0) {
            messageContent += `\n\n图片链接：\n${newImageUrls.map(url => `![图片](${url})`).join('\n')}`;
          }
          session.send(messageContent);
        } else {
          session.send('返回的内容不包含文本信息。');
        }
      } catch (error) {
        session.send(`发送请求失败: ${error.message}`);
      }
    });
}
