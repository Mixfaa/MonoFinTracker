Financial tracker for Monobank account, (https://api.monobank.ua/docs/index.html)

Now implemented:
- user registration
- mono api webhook to receive updates
- payments information processing (saving to monodb)

Technologies used:
- OpenFeign for monobank api client
- WebHook to receive user tx as events
- MonoDB as database
- Spring Security for authenticating users
- Docker (compose) for databse
