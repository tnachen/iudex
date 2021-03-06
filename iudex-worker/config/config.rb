
module LocalIudexConfig
  include RJack

  Logback[ 'iudex.filter.core.FilterChain.agent' ].level = Logback::DEBUG

  Iudex.configure do |c|

    threads = 3

    c.connect_props = {
      :ds_pool  => { :max_active => threads / 3 * 2,
                     :max_idle   => threads / 3 },
      :loglevel => 1
    }

    c.setup_http_client_3 do |mgr|
      mgr.manager_params.max_total_connections = threads * 10
    end

    c.setup_visit_executor do |vx|
      vx.max_threads = threads
      vx.min_host_delay = 100 #ms
    end

    c.setup_work_poller do |wp|
      wp.min_order_remaining_ratio = 0.30
      wp.max_check_interval = 100 #ms
      wp.min_poll_interval = 2_000 #ms
    end

    c.setup_filter_factory do |ff|

      def ff.barc_writer
        bw = super
        bw.do_compress = false
        bw
      end

      def ff.barc_directory
        bdir = super
        bdir.target_length = 2 * ( 1024 ** 2 )
        bdir
      end

    end

  end

end
