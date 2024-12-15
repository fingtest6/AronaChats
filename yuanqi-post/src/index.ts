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

// 应用插件
export function apply(ctx: Context, config: Config) {
  ctx.command( config.prefix, '发送消息给API')
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
        const content = result.choices[0].message.content;
        session.send(`${content}`);
      } catch (error) {
        session.send(`发送请求失败: ${error.message}`);
      }
    });
}
