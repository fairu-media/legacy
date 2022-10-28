/** @type {import('next').NextConfig} */
const nextConfig = {
  reactStrictMode: true,
  swcMinify: true,
  images: {
    domains: [ "127.0.0.1", "img.2d.gay" ]
  },
  basePath: "/web",
  output: 'standalone',
}

module.exports = nextConfig
