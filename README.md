# About

[実践テスト駆動開発](https://www.shoeisha.co.jp/book/detail/9784798124582) の AuctionSniper をトレースするリポジトリ

## XMPP server, Openfire

docker image を作成し、docker compose で postgresql と連携させて起動させる。

### create docker image

```
git clone https://github.com/igniterealtime/Openfire.git
cd Openfire
git checkout v4.7.4
chmod u+x mvnw
./mvnw verify
docker build . -t openfire:4.7.4
```

this images is pushed to ghcr.io, as `ghcr.io/pojiro/openfire:4.7.4`.
docker compose uses it.

### start server

```
cd this_repo
docker compose up
```

open browser http://localhost:9090, then setup server setting

p.93 の「Openfire サーバーをセットアップする」を忘れずにやる

## Window Licker

copy windowlicker-core-DEV.jar and windowlicker-swing-DEV.jar from https://github.com/tomykaira/auctionSniper/tree/new/lib/develop to lib/

### suppress keyboard layout error

use `-Dcom.objogate.wl.keyboard=US`, this property is read from windowlicker
