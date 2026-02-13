# 1. Use a more modern, supported version of Mongo
FROM mongo:5.0

# 2. Add your initialization script and data dump
# In K8s, we usually handle 'dump' files via volumes, but this works for a custom image
ADD init.sh /init.sh
ADD ./dump /dump

# 3. Clean up the RUN command
# 'apt-get dist-upgrade --force-yes' is deprecated. We use 'wget' or 'curl' if needed.
# We keep dos2unix to ensure the script runs even if edited on Windows.
RUN apt-get update && \
    apt-get install -y dos2unix psmisc && \
    dos2unix /init.sh && \
    chmod +x /init.sh && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*

ENTRYPOINT ["/init.sh"]